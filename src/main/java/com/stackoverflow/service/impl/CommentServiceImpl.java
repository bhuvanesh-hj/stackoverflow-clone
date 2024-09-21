package com.stackoverflow.service.impl;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.CommentDetailsDTO;
import com.stackoverflow.dto.CommentRequestDTO;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public String createComment(CommentRequestDTO commentRequestDTO, Long questionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Comment comment = modelMapper.map(commentRequestDTO,Comment.class);

        comment.setAuthor(author);
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

}
