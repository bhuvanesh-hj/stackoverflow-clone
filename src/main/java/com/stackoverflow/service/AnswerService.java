package com.stackoverflow.service;

import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public interface AnswerService {

    public String createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId);

    public Answer getAnswerById(Long answerId);

    public void update(Long answerId, AnswerRequestDTO answerRequestDTO);

    public void delete(Long answerId);

    public Question getQuestionById(Long questionId);

}
