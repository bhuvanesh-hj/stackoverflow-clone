package com.stackoverflow.controller;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public String getAllTags(Model model) {
        List<TagDTO> tagsWithCount = tagService.getAllTagsWithQuestionCount();
        model.addAttribute("tags", tagsWithCount);
        return "tag";
    }
}
