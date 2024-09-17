package com.stackoverflow.service.impl;

import com.stackoverflow.entity.*;
import com.stackoverflow.repository.AnswerVoteRepository;
import com.stackoverflow.repository.QuestionVoteRepository;
import com.stackoverflow.service.VoteService;
import jakarta.transaction.Transactional;

import java.util.Optional;

public class VoteServiceImpl implements VoteService {

    private final QuestionVoteRepository questionVoteRepository;
    private final AnswerVoteRepository answerVoteRepository;

    public VoteServiceImpl(QuestionVoteRepository questionVoteRepository, AnswerVoteRepository answerVoteRepository) {
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
    }

    @Override
    @Transactional
    public void upvoteQuestion(Long questionId, User user) {
        Optional<QuestionVote> existingVote = questionVoteRepository.findByQuestionIdAndUserId(questionId, user.getId());

        if (existingVote.isPresent()) {
            QuestionVote vote = existingVote.get();
            if (vote.getIsUpvote()) {
                questionVoteRepository.delete(vote);  // Undo upvote
            } else {
                vote.setIsUpvote(true);  // Change from downvote to upvote
                questionVoteRepository.save(vote);
            }
        } else {
            QuestionVote vote = new QuestionVote();
            vote.setQuestion(new Question(questionId));
            vote.setUser(user);
            vote.setIsUpvote(true);
            questionVoteRepository.save(vote);
        }
    }

    @Override
    @Transactional
    public void downvoteQuestion(Long questionId, User user) {
        Optional<QuestionVote> existingVote = questionVoteRepository.findByQuestionIdAndUserId(questionId, user.getId());

        if (existingVote.isPresent()) {
            QuestionVote vote = existingVote.get();
            if (!vote.getIsUpvote()) {
                questionVoteRepository.delete(vote);  // Undo downvote
            } else {
                vote.setIsUpvote(false);  // Change from upvote to downvote
                questionVoteRepository.save(vote);
            }
        } else {
            QuestionVote vote = new QuestionVote();
            vote.setQuestion(new Question(questionId));  // Assuming you have a constructor that sets the ID
            vote.setUser(user);
            vote.setIsUpvote(false);
            questionVoteRepository.save(vote);
        }
    }

    @Override
    @Transactional
    public void upvoteAnswer(Long answerId, User user) {
        Optional<AnswerVote> existingVote = answerVoteRepository.findByAnswerIdAndUserId(answerId, user.getId());

        if (existingVote.isPresent()) {
            AnswerVote vote = existingVote.get();
            if (vote.getIsUpvote()) {
                answerVoteRepository.delete(vote);  // Undo upvote
            } else {
                vote.setIsUpvote(true);  // Change from downvote to upvote
                answerVoteRepository.save(vote);
            }
        } else {
            AnswerVote vote = new AnswerVote();
            vote.setAnswer(new Answer(answerId));
            vote.setUser(user);
            vote.setIsUpvote(true);
            answerVoteRepository.save(vote);
        }
    }

    @Override
    @Transactional
    public void downvoteAnswer(Long answerId, User user) {
        Optional<AnswerVote> existingVote = answerVoteRepository.findByAnswerIdAndUserId(answerId, user.getId());

        if (existingVote.isPresent()) {
            AnswerVote vote = existingVote.get();
            if (!vote.getIsUpvote()) {
                answerVoteRepository.delete(vote);  // Undo downvote
            } else {
                vote.setIsUpvote(false);  // Change from upvote to downvote
                answerVoteRepository.save(vote);
            }
        } else {
            AnswerVote vote = new AnswerVote();
            vote.setAnswer(new Answer(answerId));  // Assuming you have a constructor that sets the ID
            vote.setUser(user);
            vote.setIsUpvote(false);
            answerVoteRepository.save(vote);
        }
    }

    @Override
    public int getQuestionScore(Long questionId) {
        int upvotes = questionVoteRepository.countByQuestionIdAndIsUpvote(questionId, true);
        int downvotes = questionVoteRepository.countByQuestionIdAndIsUpvote(questionId, false);
        return upvotes - downvotes;
    }

    @Override
    public int getAnswerScore(Long answerId) {
        int upvotes = answerVoteRepository.countByAnswerIdAndIsUpvote(answerId, true);
        int downvotes = answerVoteRepository.countByAnswerIdAndIsUpvote(answerId, false);
        return upvotes - downvotes;
    }
}
