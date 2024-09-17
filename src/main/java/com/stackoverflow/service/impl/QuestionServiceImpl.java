package com.stackoverflow.service.impl;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {
    @Override
    public List<QuestionDetailsDTO> getAllQuestions() {
        return List.of();
    }

    @Override
    public QuestionDetailsDTO getQuestionById(Long questionId) {
        return null;
    }

    @Override
    public QuestionDetailsDTO createQuestion(QuestionRequestDTO question) {
        return null;
    }

    @Override
    public QuestionDetailsDTO updateQuestion(Long userId, QuestionRequestDTO updatedUserDetails) {
        return null;
    }

    @Override
    public Boolean deleteQuestion(Long userId) {
        return null;
    }
}
