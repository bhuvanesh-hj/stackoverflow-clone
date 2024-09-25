package com.stackoverflow.service.impl;

import com.stackoverflow.dto.comments.CommentDetailsDTO;
import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.CommentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, QuestionRepository questionRepository, AnswerRepository answerRepository,
                              ModelMapper modelMapper, UserServiceImpl userService, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void commentOnQuestion(CommentRequestDTO commentRequestDTO, Long questionId) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        user.setReputations(user.getReputations() + 2);
        userRepository.save(user);

        Comment comment = modelMapper.map(commentRequestDTO, Comment.class);

        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        if (questionId != null) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            comment.setQuestion(question);
        }
        commentRepository.save(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }


    @Override
    public void updateComment(Long commentId, CommentRequestDTO commentRequestDTO, Long questionId) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        Comment existingComment = getCommentById(commentId);

        existingComment.setComment(commentRequestDTO.getComment());
        existingComment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(existingComment);
    }

    public void deleteComment(Long commentId) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        user.setReputations(user.getReputations() - 2);
        userRepository.save(user);

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void commentOnAnswer(CommentRequestDTO commentRequestDTO, Long answerId) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        user.setReputations(user.getReputations() + 2);
        userRepository.save(user);

        Comment comment = modelMapper.map(commentRequestDTO, Comment.class);

        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        if (answerId != null) {
            Answer answer = answerRepository.findById(answerId)
                    .orElseThrow(() -> new RuntimeException("Answer not found"));
            comment.setAnswer(answer);
        }

        commentRepository.save(comment);

    }

    @Override
    @Transactional
    public void commentOnAnswerComment(CommentRequestDTO commentRequestDTO, Long answerId, Long commentId) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        user.setReputations(user.getReputations() + 1);
        userRepository.save(user);

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        Comment comment = modelMapper.map(commentRequestDTO, Comment.class);
        comment.setUpdatedAt(LocalDateTime.now());

        comment.setAuthor(user);
        comment.setParentComment(parentComment);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public void commentOnQuestionComment(CommentRequestDTO commentRequestDTO, Long questionId, Long commentId) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        user.setReputations(user.getReputations() + 1);
        userRepository.save(user);

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question comment not found"));

        Comment comment = modelMapper.map(commentRequestDTO, Comment.class);

        comment.setAuthor(user);
        comment.setParentComment(parentComment);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }


}
