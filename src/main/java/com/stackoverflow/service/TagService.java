package com.stackoverflow.service;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.entity.Tag;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TagService {
    Page<TagDTO> getAllTagsWithQuestionCount(int page, int size, String sort, String searchQuery);

    List<String> getRecentTags();
}
