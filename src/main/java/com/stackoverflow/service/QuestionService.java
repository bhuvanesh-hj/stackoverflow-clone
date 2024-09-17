package com.stackoverflow.service;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;

import java.util.List;

public interface QuestionService {
    public List<QuestionDetailsDTO> getAllQuestions();

    public QuestionDetailsDTO getQuestionById(Long questionId);

    public QuestionDetailsDTO createQuestion(QuestionRequestDTO question);

    public QuestionDetailsDTO updateQuestion(Long userId, QuestionRequestDTO updatedUserDetails);

    public Boolean deleteQuestion(Long userId);
}
