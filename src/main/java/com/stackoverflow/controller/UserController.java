package com.stackoverflow.controller;

import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.dto.user.UserRegistrationDTO;
import com.stackoverflow.dto.user.UserUpdateDTO;
import com.stackoverflow.dto.user.UserViewDTO;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceAlreadyExistsException;
import com.stackoverflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/users")
@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult, Model model) {
        List<String> errorsList = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            errorsList = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors_register", errorsList);
            return "users/register";
        }

        try {
            userService.createUser(userRegistrationDTO);
        } catch (ResourceAlreadyExistsException e) {
            errorsList.add(e.getMessage());
            model.addAttribute("errors_register", errorsList);
            return "users/register";
        }

        return "redirect:/users/login";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") Long userId, Model model) {
        UserDetailsDTO userDetails = userService.getUserById(userId);
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("loggedIn",userService.getLoggedInUser());
        return "users/profile";
    }

    @GetMapping("/update/{id}")
    public String updateUserForm(@PathVariable("id") Long userId, Model model) {
        UserDetailsDTO userDetails = userService.getUserById(userId);
        model.addAttribute("userDetails", userDetails);
        return "users/update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long userId,
                             @Valid @ModelAttribute("userRegistrationDTO") UserUpdateDTO userUpdateDTO,
                             BindingResult bindingResult, Model model) {
        List<String> errorsList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorsList = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors_update", errorsList);
            return "users/update";
        }
        userService.updateUser(userId, userUpdateDTO);
        return "redirect:/users/" + userId;
    }

    @GetMapping("/change-password/{id}")
    public String changePasswordForm(@PathVariable("id") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "users/change_password_page";
    }

    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable("id") Long userId,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        try {
            userService.updatePassword(userId, oldPassword, newPassword);
            return "redirect:/users/" + userId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "users/change_password_page";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/users";
    }

    @GetMapping
    public String getUsers(Model model) {
        List<UserViewDTO> users = userService.getAllUsersWithCounts();
        model.addAttribute("users", users);
        model.addAttribute("questions", null);
        model.addAttribute("tags", null);
        model.addAttribute("loggedIn",userService.getLoggedInUser());

        return "user";
    }

}
