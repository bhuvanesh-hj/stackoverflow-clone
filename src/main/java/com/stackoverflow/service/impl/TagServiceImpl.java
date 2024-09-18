package com.stackoverflow.service.impl;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.service.TagService;
import com.stackoverflow.entity.Tag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagDTO> getAllTagsWithQuestionCount() {
        List<Object[]> results = tagRepository.findAllTagsWithQuestionCount();
        List<TagDTO> tagsWithCount = new ArrayList<>();

        for (Object[] result : results) {
            Tag tag = (Tag) result[0];
            Long questionCount = (Long) result[1];
            tagsWithCount.add(new TagDTO(tag.getName(), questionCount));
        }
        return tagsWithCount;
    }
}
