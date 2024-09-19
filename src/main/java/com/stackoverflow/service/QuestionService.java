package com.stackoverflow.service;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.entity.Question;

import java.util.List;

public interface QuestionService {
    public List<Question> getAllQuestions();

    public QuestionDetailsDTO getQuestionById(Long questionId);

    public QuestionDetailsDTO createQuestion(QuestionRequestDTO question);

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails);

    public Boolean deleteQuestion(Long questionId);
}
