package com.stackoverflow.controller;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.service.TagService;
import com.stackoverflow.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String getAllTags(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "1") int size,
                             @RequestParam(value = "sort", defaultValue = "desc") String sort,
                             @RequestParam(value = "query", defaultValue = "") String searchQuery,
                             Model model) {
        Page<TagDTO> tagsWithCount = tagService.getAllTagsWithQuestionCount(page, size, sort, searchQuery);

        model.addAttribute("tags", tagsWithCount);
        model.addAttribute("questions", null);
        model.addAttribute("users", null);
        model.addAttribute("page",page);
        model.addAttribute("total_pages", size);
        model.addAttribute("sort", sort);
        model.addAttribute("query", searchQuery);
        model.addAttribute("loggedIn", userService.getLoggedInUserOrNull());

        return "tags/tag";
    }
}
