package com.stackoverflow.service;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.entity.Question;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuestionService {
    public Page<QuestionDetailsDTO> getAllQuestions(int page, int size, String sort);

    public QuestionDetailsDTO getQuestionById(Long questionId);

    public QuestionDetailsDTO createQuestion(QuestionRequestDTO question);

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails);

    public Boolean deleteQuestion(Long questionId);

    public QuestionDetailsDTO  vote(Boolean isUpvote, Long questionId, Long userId);

    public void saveQuestionForUser(Long questionId);

    public void unsaveQuestionForUser(Long questionId);

    public List<QuestionDetailsDTO> getQuestionsByUser(Long id);

    public List<QuestionDetailsDTO> getSavedQuestionsByUser(Long id);

   public Page<QuestionDetailsDTO> getSearchedQuestions(String keyword, int page,int size,String sort);
    public List<QuestionDetailsDTO> getAnsweredQuestions(Long id);


    public QuestionDetailsDTO getQuestionDetailsDTO(Question question);
}
