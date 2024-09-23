package com.stackoverflow.service;

import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.dto.questions.QuestionRequestDTO;
import com.stackoverflow.entity.Question;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuestionService {

    Page<QuestionDetailsDTO> getAllQuestions(int page, int size, String sort);

    QuestionDetailsDTO getQuestionById(Long questionId);

    QuestionDetailsDTO createQuestion(QuestionRequestDTO question);

    QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails);

    Boolean deleteQuestion(Long questionId);

    QuestionDetailsDTO vote(Boolean isUpvote, Long questionId, Long userId);

    void saveQuestionForUser(Long questionId);

    void unsaveQuestionForUser(Long questionId);

    List<QuestionDetailsDTO> getQuestionsByUserId(Long id);

    List<QuestionDetailsDTO> getSavedQuestionsByUserId(Long id);

    List<QuestionDetailsDTO> getAnsweredQuestionsByUserId(Long id);

    Page<QuestionDetailsDTO> getSearchedQuestions(String keyword, int page, int size, String sort);

    QuestionDetailsDTO getQuestionDetailsDTO(Question question);

    List<QuestionDetailsDTO> getRelatedQuestionsByTags(List<String> tags, Long questionId);

}
