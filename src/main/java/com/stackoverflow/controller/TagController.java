package com.stackoverflow.controller;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.service.TagService;
import com.stackoverflow.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String getAllTags(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "15") int size,
                             @RequestParam(value = "sort", defaultValue = "desc") String sort,
                             @RequestParam(value = "search", defaultValue = "") String searchQuery,
                             Model model) {
        Page<TagDTO> tagsWithCount = tagService.getAllTagsWithQuestionCount(page, size, sort, searchQuery);

        model.addAttribute("tags", tagsWithCount.getContent());
        model.addAttribute("questions", null);
        model.addAttribute("users", null);
        model.addAttribute("current_page", page);
        model.addAttribute("total_pages", tagsWithCount.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("search", searchQuery);
        model.addAttribute("loggedIn", userService.getLoggedInUserOrNull());

        return "tags/tag";
    }
}
