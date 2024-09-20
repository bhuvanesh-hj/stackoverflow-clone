package com.stackoverflow.service.impl;

import com.stackoverflow.entity.*;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.AnswerVoteRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.QuestionVoteRepository;
import com.stackoverflow.service.UserService;
import com.stackoverflow.service.VoteService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("voteService")
public class VoteServiceImpl implements VoteService {

    private final QuestionVoteRepository questionVoteRepository;
    private final AnswerVoteRepository answerVoteRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final UserService userService;

    public VoteServiceImpl(QuestionVoteRepository questionVoteRepository, AnswerVoteRepository answerVoteRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, UserService userService) {
        this.questionVoteRepository = questionVoteRepository;
        this.answerVoteRepository = answerVoteRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void upvoteQuestion(Long questionId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Optional<QuestionVote> existingVote = questionVoteRepository.findByQuestionIdAndUserId(questionId, user.getId());
        if (existingVote.isPresent()) {
            QuestionVote vote = existingVote.get();
            if (vote.getIsUpvote()) {
                questionVoteRepository.delete(vote);
            } else {
                vote.setIsUpvote(true);
                questionVoteRepository.save(vote);
            }
        } else {
            QuestionVote vote = new QuestionVote();
            vote.setQuestion(question);
            vote.setUser(user);
            vote.setIsUpvote(true);
            questionVoteRepository.save(vote);
        }
    }

    @Override
    @Transactional
    public void downvoteQuestion(Long questionId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Optional<QuestionVote> existingVote = questionVoteRepository.findByQuestionIdAndUserId(questionId, user.getId());
        if (existingVote.isPresent()) {
            QuestionVote vote = existingVote.get();
            if (!vote.getIsUpvote()) {
                questionVoteRepository.delete(vote);
            } else {
                vote.setIsUpvote(false);
                questionVoteRepository.save(vote);
            }
        } else {
            QuestionVote vote = new QuestionVote();
            vote.setQuestion(question);
            vote.setUser(user);
            vote.setIsUpvote(false);
            questionVoteRepository.save(vote);
        }
    }

    @Override
    @Transactional
    public void upvoteAnswer(Long answerId, Long userId) {
        User user = userService.getLoggedInUser();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        Optional<AnswerVote> existingVote = answerVoteRepository.findByAnswerIdAndUserId(answerId, userId);

        if (existingVote.isPresent()) {
            AnswerVote vote = existingVote.get();
            if (vote.getIsUpvote()) {
                answerVoteRepository.delete(vote);
            } else {
                vote.setIsUpvote(true);
                answerVoteRepository.save(vote);
            }
        } else {
            AnswerVote vote = new AnswerVote();
            vote.setAnswer(answer);
            vote.setUser(user);
            vote.setIsUpvote(true);
            answerVoteRepository.save(vote);
        }
    }

    @Override
    @Transactional
    public void downvoteAnswer(Long answerId, Long userId) {
        User user = userService.getLoggedInUser();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        Optional<AnswerVote> existingVote = answerVoteRepository.findByAnswerIdAndUserId(answerId, userId);

        if (existingVote.isPresent()) {
            AnswerVote vote = existingVote.get();
            if (!vote.getIsUpvote()) {
                answerVoteRepository.delete(vote);
            } else {
                vote.setIsUpvote(false);
                answerVoteRepository.save(vote);
            }
        } else {
            AnswerVote vote = new AnswerVote();
            vote.setAnswer(answer);
            vote.setUser(user);
            vote.setIsUpvote(false);
            answerVoteRepository.save(vote);
        }
    }

    @Override
    public int getQuestionUpvotes(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        int count = questionVoteRepository.countByQuestionIdAndIsUpvoteTrue(questionId);
        return count;
    }

    @Override
    public int getQuestionDownvotes(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        int count = questionVoteRepository.countByQuestionIdAndIsUpvoteFalse(questionId);
        return count;
    }

    @Override
    public int getAnswerUpvotes(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();
        int count = answerVoteRepository.countByAnswerIdAndIsUpvoteTrue(answerId);
        return count;
    }

    @Override
    public int getAnswerDownvotes(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        int count = answerVoteRepository.countByAnswerIdAndIsUpvoteFalse(answerId);
        return count;
    }

}
