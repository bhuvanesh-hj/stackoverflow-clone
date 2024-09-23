package com.stackoverflow.service.impl;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.entity.Tag;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<TagDTO> getAllTagsWithQuestionCount(int page, int size, String sort, String searchQuery) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "createdAt"));
        Page<Tag> results = tagRepository.findAllTagsWithQuestionCount(searchQuery, pageable);

        return results.map(tag -> {
            int questionCount = tag.getQuestions().size();
            return new TagDTO(tag.getName(), questionCount);
        });
    }

    @Override
    public List<String> getRecentTags() {
        return tagRepository.findRecentTags();
    }
}
