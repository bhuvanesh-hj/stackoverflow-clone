package com.stackoverflow.service;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.answers.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;

import java.util.List;

public interface AnswerService {

    public AnswerDetailsDTO createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId);

    public AnswerDetailsDTO getAnswerById(Long answerId);

    public AnswerDetailsDTO update(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO);

    public Boolean delete(Long answerId);

    public List<AnswerDetailsDTO> getAnswersByUser(Long id);

    public AnswerDetailsDTO getAnswerDetailsDTO(Answer answer);

}