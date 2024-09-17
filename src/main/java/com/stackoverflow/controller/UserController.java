package com.stackoverflow.controller;

import com.stackoverflow.dto.UserDetailsDTO;
import com.stackoverflow.dto.UserRegistrationDTO;
import com.stackoverflow.dto.UserUpdateDTO;
import com.stackoverflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login_page";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "register_page";
    }

    @PostMapping("/register")
    public String register(@Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register_page";
        }

        userService.createUser(userRegistrationDTO);
        return "redirect:/users/login";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") Long userId, Model model) {
        UserDetailsDTO userDetails = userService.getUserById(userId);
        model.addAttribute("userDetails", userDetails);
        return "user_details_page";
    }

    @GetMapping("/update/{id}")
    public String updateUserForm(@PathVariable("id") Long userId, Model model) {
        UserDetailsDTO userDetails = userService.getUserById(userId);
        model.addAttribute("userDetails", userDetails);
        return "update_user_page";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long userId,
                             @Valid @ModelAttribute("userRegistrationDTO") UserUpdateDTO userUpdateDTO,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDetails", userUpdateDTO);
            return "update_user_page";
        }
        userService.updateUser(userId, userUpdateDTO);
        return "redirect:/users/" + userId;
    }

    @GetMapping("/change-password/{id}")
    public String changePasswordForm(@PathVariable("id") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "change_password_page";
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
            return "change_password_page";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/users";
    }

}
