package com.stackoverflow.service;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.answers.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;

import java.util.List;

public interface AnswerService {

    AnswerDetailsDTO createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId);

    AnswerDetailsDTO getAnswerById(Long answerId);

    AnswerDetailsDTO updateAnswer(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO);

    Boolean deleteAnswer(Long answerId);

    List<AnswerDetailsDTO> getAnswersByUserId(Long id);

    AnswerDetailsDTO getAnswerDetailsDTO(Answer answer);

}