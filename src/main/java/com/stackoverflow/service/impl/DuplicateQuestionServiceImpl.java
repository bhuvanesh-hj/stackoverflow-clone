package com.stackoverflow.service.impl;

import com.stackoverflow.repository.QuestionRepository;
import org.apache.commons.text.similarity.FuzzyScore;
import com.stackoverflow.service.DuplicateQuestionService;
import org.springframework.stereotype.Service;
import com.stackoverflow.entity.Question;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DuplicateQuestionServiceImpl implements DuplicateQuestionService {
    private final QuestionRepository questionRepository;

    public DuplicateQuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public boolean isDuplicate(String newQuestion) {
        FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);
        List<String> existingQuestions = questionRepository.findAll()
                .stream()
                .map(Question::getTitle)
                .collect(Collectors.toList());

        for (String question : existingQuestions) {
            int score = fuzzyScore.fuzzyScore(newQuestion, question);
            if (score > 80) {
                return true;
            }
        }
        return false;
    }
}
