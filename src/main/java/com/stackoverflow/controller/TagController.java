package com.stackoverflow.controller;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.service.TagService;
import com.stackoverflow.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TagController {
    private final TagService tagService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public TagController(TagService tagService, UserService userService, ModelMapper modelMapper) {
        this.tagService = tagService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/tags")
    public String getAllTags(Model model) {
        List<TagDTO> tagsWithCount = tagService.getAllTagsWithQuestionCount();

        model.addAttribute("tags", tagsWithCount);
        model.addAttribute("questions", null);
        model.addAttribute("users", null);
        if (userService.isUserLoggedIn()) {
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));
        } else {
            model.addAttribute("loggedIn", null);
        }

        return "tags/tag";
    }
}
