package com.stackoverflow.service.impl;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.comments.CommentDetailsDTO;
import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    public CommentServiceImpl(CommentRepository commentRepository, QuestionRepository questionRepository, AnswerRepository answerRepository,
                              ModelMapper modelMapper, UserRepository userRepository, UserServiceImpl userService) {
        this.commentRepository = commentRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public String createComment(CommentRequestDTO commentRequestDTO, Long questionId) {
        User user = userService.getLoggedInUser();

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

        return StackoverflowCloneApplication.formatTime(comment.getCreatedAt());
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }


    @Override
    public void update(Long commentId, CommentRequestDTO commentRequestDTO, Long questionId) {
        Comment existingComment = getCommentById(commentId);

        existingComment.setComment(commentRequestDTO.getComment());
        existingComment.setUpdatedAt(LocalDateTime.now());

        if (questionId != null) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            existingComment.setQuestion(question);
        }

        Comment updatedComment = commentRepository.save(existingComment);

        CommentDetailsDTO commentDetailsDTO = modelMapper.map(updatedComment, CommentDetailsDTO.class);

    }

    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public String createComment(CommentRequestDTO commentRequestDTO, Long questionId, Long commentId) {
        User user = userService.getLoggedInUser();

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

        return StackoverflowCloneApplication.formatTime(comment.getCreatedAt());

    }


}
