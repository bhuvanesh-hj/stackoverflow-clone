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
import java.util.stream.Collectors;

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
        QuestionDetailsDTO q = modelMapper.map(questionRepository.findById(questionId), QuestionDetailsDTO.class);
        System.out.println(q);
        return q;
        //return modelMapper.map(questionRepository.findById(questionId), QuestionDetailsDTO.class);
    }

    @Override
    @Transactional
    public QuestionDetailsDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        // Validate the user
        User user = userService.getLoggedInUser();
        if (user == null) {
            // Return error message or redirect to login page
            throw new RuntimeException("User not logged in");
        }

        Question question = modelMapper.map(questionRequestDTO, Question.class);
        question.setAuthor(user);

        Set<Tag> tags = questionRequestDTO.getTagsList().stream()
                .map(tagName ->tagRepository.findByName(tagName).orElseGet(() -> new Tag(tagName))
        ).collect(Collectors.toSet());

        question.setTags(tags);

        Question updatedQuestion = questionRepository.save(question);
        return modelMapper.map(updatedQuestion, QuestionDetailsDTO.class);
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
