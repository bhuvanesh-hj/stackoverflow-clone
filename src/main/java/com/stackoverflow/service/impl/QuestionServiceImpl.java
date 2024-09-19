package com.stackoverflow.service.impl;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.entity.*;
import com.stackoverflow.exception.ResourceNotFoundException;
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
    public List<QuestionDetailsDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(question -> getQuestionDetailsDTO(question))
                .collect(Collectors.toList());
    }

    @Override
    public QuestionDetailsDTO getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        return getQuestionDetailsDTO(question);
    }

    @Override
    @Transactional
    public QuestionDetailsDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        User user = userService.getLoggedInUser();

        Question question = modelMapper.map(questionRequestDTO, Question.class);
        question.setAuthor(user);

        Set<Tag> tags = questionRequestDTO.getTagsList().stream()
                .map(tagName ->tagRepository.findByName(tagName).orElseGet(() -> new Tag(tagName))
        ).collect(Collectors.toSet());

        question.setTags(tags);

        Question updatedQuestion = questionRepository.save(question);
        return getQuestionDetailsDTO(updatedQuestion);
    }

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() ->  new RuntimeException("Question not found"));

        existingQuestion.setTitle(updatedUserDetails.getTitle());
        existingQuestion.setBody(updatedUserDetails.getBody());
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        Question updatedQuestion = questionRepository.save(existingQuestion);

        return getQuestionDetailsDTO(updatedQuestion);
    }

    @Override
    public Boolean deleteQuestion(Long questionId) {
        User user = userService.getLoggedInUser();
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() ->  new RuntimeException("Question not found"));

        questionRepository.deleteById(questionId);
        return true;
    }

    @Override
    public QuestionDetailsDTO vote(Boolean isUpvote, Long questionId, Long userId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        QuestionVote upvote = new QuestionVote(isUpvote?true:false, question, user);
        question.getQuestionVotes().add(upvote);
        Question updateQuestion = questionRepository.save(question);

        return getQuestionDetailsDTO(question);
    }

    public QuestionDetailsDTO getQuestionDetailsDTO(Question question){
        int upvotes = 0;
        int downvotes = 0;

        for(QuestionVote questionVote : question.getQuestionVotes()){
            if(questionVote.getIsUpvote()){
                ++upvotes;
            }else{
                ++downvotes;
            }
        }

        QuestionDetailsDTO questionDetailsDTO = modelMapper.map(question, QuestionDetailsDTO.class);

        questionDetailsDTO.setAnswersCount(question.getAnswers().size());
        questionDetailsDTO.setUpvotes(upvotes);
        questionDetailsDTO.setDownvotes(downvotes);

        return questionDetailsDTO;
    }
}
