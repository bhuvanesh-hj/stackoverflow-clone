package com.stackoverflow.service;

import com.stackoverflow.entity.User;

public interface VoteService {
    void upvoteQuestion(Long questionId, User user);
    void downvoteQuestion(Long questionId, User user);
    void upvoteAnswer(Long answerId, User user);
    void downvoteAnswer(Long answerId, User user);
    int getQuestionScore(Long questionId);  // Upvotes - Downvotes
    int getAnswerScore(Long answerId);
}
