package com.stackoverflow.service;

import com.stackoverflow.dto.TagDTO;

import java.util.List;

public interface TagService {
    List<TagDTO> getAllTagsWithQuestionCount();
}
