package com.stackoverflow.service;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.entity.Question;

import java.util.List;

public interface QuestionService {
    public List<QuestionDetailsDTO> getAllQuestions();

    public QuestionDetailsDTO getQuestionById(Long questionId);

    public QuestionDetailsDTO createQuestion(QuestionRequestDTO question);

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails);

    public Boolean deleteQuestion(Long questionId);

    public QuestionDetailsDTO  vote(Boolean isUpvote, Long questionId, Long userId);

    public void saveQuestionForUser(Long questionId);

    public void unsaveQuestionForUser(Long questionId);

    public List<QuestionDetailsDTO> getQuestionsByUser(Long id);

    public List<QuestionDetailsDTO> getSavedQuestionsByUser(Long id);

//    public List<Question> getSearchedQuestions(String keyword);
}
