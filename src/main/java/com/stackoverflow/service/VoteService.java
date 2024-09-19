package com.stackoverflow.service;

public interface VoteService {
    void upvoteQuestion(Long questionId, Long userId);
    void downvoteQuestion(Long questionId, Long userId);
    void upvoteAnswer(Long answerId, Long userId);
    void downvoteAnswer(Long answerId, Long userId);
    int getQuestionUpvotes(Long questionId);
    int getQuestionDownvotes(Long questionId);
    int getAnswerUpvotes(Long questionId);
    int getAnswerDownvotes(Long questionId);
}