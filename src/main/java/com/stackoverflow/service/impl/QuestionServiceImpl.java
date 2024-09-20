package com.stackoverflow.service.impl;

import com.stackoverflow.dto.*;
import com.stackoverflow.entity.*;

import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.VoteService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final UserServiceImpl userService;
    private final VoteService voteService;
    private final UserRepository userRepository;
    private final AnswerService answerService;

    public QuestionServiceImpl(QuestionRepository questionRepository, TagRepository tagRepository, AnswerRepository answerRepository,
                               ModelMapper modelMapper, UserServiceImpl userService, VoteService voteService, UserRepository userRepository, AnswerService answerService) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.voteService = voteService;
        this.userRepository = userRepository;
        this.answerService = answerService;
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
                .map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> new Tag(tagName)))
                .collect(Collectors.toSet());

        question.setTags(tags);

        Question updatedQuestion = questionRepository.save(question);
        return getQuestionDetailsDTO(updatedQuestion);
    }

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

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
                .orElseThrow(() -> new RuntimeException("Question not found"));

        questionRepository.deleteById(questionId);
        return true;
    }

    @Override
    public QuestionDetailsDTO vote(Boolean isUpvote, Long questionId, Long userId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        QuestionVote upvote = new QuestionVote(isUpvote, question, user);
        question.getQuestionVotes().add(upvote);
        Question updateQuestion = questionRepository.save(question);

        return getQuestionDetailsDTO(question);
    }

    @Transactional
    public void saveQuestionForUser(Long questionId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        user.getQuestionsSaved().add(question);
        question.getSavedByUsers().add(user);

        userRepository.save(user);
    }

    @Override
    public void unsaveQuestionForUser(Long questionId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        user.getQuestionsSaved().remove(question);
        question.getSavedByUsers().remove(user);

        userRepository.save(user);
    }

    @Override
    public List<QuestionDetailsDTO> getQuestionsByUser(Long userId) {
        return questionRepository.findByAuthorId(userId).stream()
                .map(question -> getQuestionDetailsDTO(question))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDetailsDTO> getSavedQuestionsByUser(Long userId) {
        return userRepository.findQuestionsSavedById(userId).stream()
                .map(question -> getQuestionDetailsDTO(question))
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDetailsDTO getQuestionDetailsDTO(Question question) {
        int upvotes = voteService.getQuestionUpvotes(question.getId());
        int downvotes = voteService.getQuestionDownvotes(question.getId());
        boolean upvoted = false;
        boolean downvoted = false;

        QuestionDetailsDTO questionDetailsDTO = modelMapper.map(question, QuestionDetailsDTO.class);

        Set<AnswerDetailsDTO> answerDetailsDTOS = question.getAnswers().stream()
                .map(answer -> answerService.getAnswerDetailsDTO(answer)).collect(Collectors.toSet());

        if (userService.isUserLoggedIn()) {
            User user = userService.getLoggedInUser();
            Integer status = questionRepository.getUserVoteStatus(question.getId(), user.getId());
            if (status != null && status == 1) {
                upvoted = true;
            } else if (status != null && status == 0) {
                downvoted = true;
            }
        }

        questionDetailsDTO.setAnswers(answerDetailsDTOS);
        questionDetailsDTO.setAnswersCount(answerRepository.countByQuestionId(questionDetailsDTO.getId()));
        questionDetailsDTO.setUpvotes(upvotes);
        questionDetailsDTO.setDownvotes(downvotes);
        questionDetailsDTO.setUpvoted(upvoted);
        questionDetailsDTO.setDownvoted(downvoted);

        return questionDetailsDTO;
    }

}
