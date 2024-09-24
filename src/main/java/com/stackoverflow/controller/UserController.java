package com.stackoverflow.controller;

import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.dto.users.UserDetailsDTO;
import com.stackoverflow.dto.users.UserRegistrationDTO;
import com.stackoverflow.dto.users.UserUpdateDTO;
import com.stackoverflow.dto.users.UserViewDTO;
import com.stackoverflow.exception.ResourceAlreadyExistsException;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.TagService;
import com.stackoverflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequestMapping("/users")
@Controller
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final TagService tagService;
    private static final String[] PROFILE_PICTURES = {
            "https://randomuser.me/api/portraits/men/14.jpg",
            "https://randomuser.me/api/portraits/men/90.jpg",
            "https://xsgames.co/randomusers/assets/avatars/pixel/39.jpg",
            "https://xsgames.co/randomusers/assets/avatars/pixel/48.jpg",
            "https://xsgames.co/randomusers/assets/avatars/pixel/1.jpg",
            "https://xsgames.co/randomusers/assets/avatars/male/17.jpg",
            "https://i.pravatar.cc/150?img=6",
            "https://i.pravatar.cc/150?img=16",
            "https://img.freepik.com/premium-photo/photorealistic-hyper-realistic-image-white-background-ai-generated-by-freepik_643360-512557.jpg?size=626&ext=jpg",
            "https://img.freepik.com/free-photo/afro-man_1368-2735.jpg?size=626&ext=jpg"
    };


    @Autowired
    public UserController(UserService userService, QuestionService questionService, TagService tagService) {
        this.userService = userService;
        this.questionService = questionService;
        this.tagService = tagService;
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
        Random random = new Random();
        int randomIndex = random.nextInt(PROFILE_PICTURES.length);

        if (bindingResult.hasErrors()) {
            errorsList = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors_register", errorsList);
            return "users/register";
        }

        try {
            String randomProfilePicture = PROFILE_PICTURES[randomIndex];
            userRegistrationDTO.setProfilePicture(randomProfilePicture);
            userService.createUser(userRegistrationDTO);
        } catch (ResourceAlreadyExistsException e) {
            errorsList.add(e.getMessage());
            model.addAttribute("errors_register", errorsList);
            return "users/register";
        }

        return "redirect:/users/login";
    }

    @GetMapping
    public String getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "15") int size,
                           @RequestParam(value = "search", defaultValue = "") String searchQuery,
                           Model model) {
        Page<UserViewDTO> paginatdUsers = userService.getAllUsersWithCounts(page, size, searchQuery);
        model.addAttribute("users", paginatdUsers.getContent());
        model.addAttribute("questions", null);
        model.addAttribute("tags", null);
        model.addAttribute("loggedIn", userService.getLoggedInUserOrNull());
        model.addAttribute("current_page", page);
        model.addAttribute("total_pages", paginatdUsers.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("search", searchQuery);
        model.addAttribute("recentTags", tagService.getRecentTags());

        return "users/user";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") Long userId, Model model) {

        try {
            model.addAttribute("loggedIn", userService.getLoggedInUser());
            UserDetailsDTO userDetails = userService.getUserById(userId);
            model.addAttribute("userDetails", userDetails);

            List<QuestionDetailsDTO> questions = questionService.getQuestionsByUserId(userId);
            List<QuestionDetailsDTO> answered = questionService.getAnsweredQuestionsByUserId(userId);
            List<QuestionDetailsDTO> saved = questionService.getSavedQuestionsByUserId(userId);

            model.addAttribute("questions", questions);
            model.addAttribute("answered", answered);
            model.addAttribute("saved", saved);
            model.addAttribute("recentTags", tagService.getRecentTags());
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            model.addAttribute("error_message", "User not found.");
            return "redirect:/users?error=" +
                    "user not found";
        }
        UserDetailsDTO userDetails = userService.getUserById(userId);
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("loggedIn", userService.getLoggedInUserOrNull());

        List<QuestionDetailsDTO> questions = questionService.getQuestionsByUserId(userId);
        List<QuestionDetailsDTO> answered = questionService.getAnsweredQuestionsByUserId(userId);
        List<QuestionDetailsDTO> saved = questionService.getSavedQuestionsByUserId(userId);

        model.addAttribute("questions", questions);
        model.addAttribute("answered", answered);
        model.addAttribute("saved", saved);
        model.addAttribute("recentTags", tagService.getRecentTags());

        return "users/profile";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable("id") Long userId,
                             @Valid @ModelAttribute("userRegistrationDTO") UserUpdateDTO userUpdateDTO,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errorsList = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            model.addAttribute("errors_update", errorsList);
            UserDetailsDTO userDetails = userService.getUserById(userId);
            model.addAttribute("userDetails", userDetails);
            model.addAttribute("loggedIn", userService.getLoggedInUserOrNull());

            return "redirect:/users/" + userId;
        }

        try {
            userService.updateUser(userId, userUpdateDTO);
        } catch (Exception e) {
            model.addAttribute("error_message", "An error occurred while updating your profile.");
            UserDetailsDTO userDetails = userService.getUserById(userId);
            model.addAttribute("userDetails", userDetails);
            model.addAttribute("loggedIn", userService.getLoggedInUserOrNull());
            return "redirect:/users/" + userId;
        }

        model.addAttribute("userDetails", userService.getUserById(userId));
        model.addAttribute("loggedIn", userService.getUserById(userId));

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
            userService.updateUserPassword(userId, oldPassword, newPassword);
            return "redirect:/users/" + userId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "users/change_password_page";
        }
    }

}
