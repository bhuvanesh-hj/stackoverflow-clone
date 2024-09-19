package com.stackoverflow.service;

import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public interface AnswerService {

    public AnswerDetailsDTO createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId);

    public AnswerDetailsDTO getAnswerById(Long answerId);

    public AnswerDetailsDTO update(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO);

    public Boolean delete(Long answerId);

}
