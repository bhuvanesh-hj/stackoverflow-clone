package com.stackoverflow.service.impl;

import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.UserService;
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
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;


    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public String createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Question question = getQuestionById(questionId);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Answer answer = modelMapper.map(answerRequestDTO, Answer.class);

        answer.setQuestion(question);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());
        answer.setAuthor(author);

        answerRepository.save(answer);
        return formatTime(answer.getCreatedAt());
    }

    public String formatTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();

        if (dateTime.toLocalDate().equals(now.toLocalDate())) {
            long minutes = ChronoUnit.MINUTES.between(dateTime, now);
            long hours = ChronoUnit.HOURS.between(dateTime, now);

            if (minutes < 1) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + " mins ago";
            } else {
                return hours + " hours ago";
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");
            return "Answered " + dateTime.format(formatter);
        }
    }

    @Override
    public Answer getAnswerById(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new NoSuchElementException("Answer not found with id: " + answerId));
    }


    public void update(Long answerId, AnswerRequestDTO answerRequestDTO) {
        Answer existingAnswer = getAnswerById(answerId);

        Answer answer = modelMapper.map(answerRequestDTO, Answer.class);

        existingAnswer.setBody(answerRequestDTO.getBody());
        existingAnswer.setUpdatedAt(LocalDateTime.now());

        Answer updatedUser = answerRepository.save(existingAnswer);
        modelMapper.map(updatedUser,AnswerDetailsDTO.class);
    }

    @Override
    public void delete(Long answerId) {
        answerRepository.deleteById(answerId);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId).get();
    }

}
