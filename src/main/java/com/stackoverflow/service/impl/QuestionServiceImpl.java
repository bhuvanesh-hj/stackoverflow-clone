package com.stackoverflow.service.impl;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.Tag;
import com.stackoverflow.entity.User;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.service.QuestionService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;
    private final UserServiceImpl userService;

    public QuestionServiceImpl(QuestionRepository questionRepository, TagRepository tagRepository,
                               ModelMapper modelMapper, UserServiceImpl userService) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public QuestionDetailsDTO getQuestionById(Long questionId) {
        return modelMapper.map(questionRepository.findById(questionId), QuestionDetailsDTO.class);
    }

    @Override
    @Transactional
    public QuestionDetailsDTO createQuestion(QuestionRequestDTO question, String tagsList) {
        // Validate the user
        User user = userService.getLoggedInUser();
        if (user == null) {
            // Return error message or redirect to login page
            throw new RuntimeException("User not logged in");
        }

        // Create and save the Question entity
        Question newQuestion = new Question();
        newQuestion.setTitle(question.getTitle());
        newQuestion.setBody(question.getBody());
        newQuestion.setAuthor(user);

        // Save the Question entity
        Question savedQuestion = questionRepository.save(newQuestion);

        // Process tags
        List<String> tagNames = Arrays.stream(tagsList.split(","))
                .map(String::trim)
                .toList();

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByTagName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setTagName(tagName);
                tagRepository.save(tag);
            }
            tag.getQuestions().add(savedQuestion);
            tags.add(tag);
        }

        // Associate tags with the question
        newQuestion.setTags(tags);
        questionRepository.save(newQuestion);


        // Map savedQuestion to QuestionDetailsDTO and return
        return modelMapper.map(savedQuestion, QuestionDetailsDTO.class);
    }

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails) {
        Optional<Question> existingQuestionOpt = questionRepository.findById(questionId);

        if (!existingQuestionOpt.isPresent()) {
            throw new RuntimeException("Question not found");
        }

        Question existingQuestion = existingQuestionOpt.get();

        existingQuestion.setTitle(updatedUserDetails.getTitle());
        existingQuestion.setBody(updatedUserDetails.getBody());
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        Question updatedQuestion = questionRepository.save(existingQuestion);

        return modelMapper.map(updatedQuestion, QuestionDetailsDTO.class);
    }

    @Override
    public Boolean deleteQuestion(Long questionId) {
        Optional<Question> existingQuestion = questionRepository.findById(questionId);

        if (!existingQuestion.isPresent()) {
            return false;
        }

        questionRepository.deleteById(questionId);
        return true;
    }
}
