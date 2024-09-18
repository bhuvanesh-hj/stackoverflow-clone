package com.stackoverflow.service.impl;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.AnswerService;
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


    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository, ModelMapper modelMapper, UserRepository userRepository, UserServiceImpl userService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public String createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId) {

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

        answerRepository.save(answer);
        return StackoverflowCloneApplication.formatTime(answer.getCreatedAt());
    }

    @Override
    public Answer getAnswerById(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new NoSuchElementException("Answer not found with id: " + answerId));
    }


    public AnswerDetailsDTO update(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO) {
        Answer existingAnswer = getAnswerById(answerId);

        existingAnswer.setBody(answerRequestDTO.getBody());
        existingAnswer.setUpdatedAt(LocalDateTime.now());

        Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
        existingAnswer.setQuestion(question);

        Answer updatedAnswer = answerRepository.save(existingAnswer);

        return modelMapper.map(updatedAnswer, AnswerDetailsDTO.class);
    }


    @Override
    public Boolean delete(Long answerId) {
        Optional<Answer> existingAnswer = answerRepository.findById(answerId);

        if(!existingAnswer.isPresent()){
            return false;
        }
        answerRepository.deleteById(answerId);
        return true;

    }

}
