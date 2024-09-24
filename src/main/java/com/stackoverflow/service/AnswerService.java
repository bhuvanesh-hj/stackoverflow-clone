package com.stackoverflow.service;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.answers.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnswerService {

    AnswerDetailsDTO createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId, boolean isAiGenerated);

    AnswerDetailsDTO getAnswerById(Long answerId);

    AnswerDetailsDTO updateAnswer(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO);

    Boolean deleteAnswer(Long answerId);

    List<AnswerDetailsDTO> getAnswersByUserId(Long id);

    AnswerDetailsDTO getAnswerDetailsDTO(Answer answer);

    Page<AnswerDetailsDTO> getSearchedAnswers(int page, int size, String sort, Long questionId);

    Boolean isAiGeneratedAnswer(String answer);

}