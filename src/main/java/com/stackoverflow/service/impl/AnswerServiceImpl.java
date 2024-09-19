package com.stackoverflow.service.impl;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.VoteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final VoteService voteService;


    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository, ModelMapper modelMapper, UserRepository userRepository, UserServiceImpl userService, VoteService voteService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userService = userService;
        this.voteService = voteService;
    }

    public AnswerDetailsDTO createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if(username == null){
            throw new UsernameNotFoundException("User name not found");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Answer answer = modelMapper.map(answerRequestDTO, Answer.class);

        answer.setQuestion(question);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());
        answer.setAuthor(author);
        Answer savedAnswer = answerRepository.save(answer);

        return modelMapper.map(savedAnswer, AnswerDetailsDTO.class);
    }

    @Override
    public AnswerDetailsDTO getAnswerById(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        AnswerDetailsDTO answerDetailsDTO = modelMapper.map(answer, AnswerDetailsDTO.class);
        answerDetailsDTO.setUpvotes(voteService.getAnswerUpvotes(answerId));
        answerDetailsDTO.setDownvotes(voteService.getAnswerUpvotes(answerId));
        return answerDetailsDTO;
    }


    public AnswerDetailsDTO update(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO) {
        Answer existingAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        existingAnswer.setBody(answerRequestDTO.getBody());
        existingAnswer.setUpdatedAt(LocalDateTime.now());

        Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
        existingAnswer.setQuestion(question);

        Answer updatedAnswer = answerRepository.save(existingAnswer);

        AnswerDetailsDTO answerDetailsDTO = modelMapper.map(updatedAnswer, AnswerDetailsDTO.class);
        answerDetailsDTO.setUpvotes(voteService.getAnswerUpvotes(answerId));
        answerDetailsDTO.setDownvotes(voteService.getAnswerUpvotes(answerId));
        return answerDetailsDTO;
    }


    @Override
    public Boolean delete(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        answerRepository.deleteById(answerId);
        return true;

    }

}