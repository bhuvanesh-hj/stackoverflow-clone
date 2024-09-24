package com.stackoverflow.service;

import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.entity.Comment;

public interface CommentService {

    void commentOnQuestion(CommentRequestDTO commentRequestDTO, Long questionId);

    void commentOnQuestionComment(CommentRequestDTO commentRequestDTO, Long questionId, Long commentId);

    Comment getCommentById(Long commentId);

    void updateComment(Long commentId, CommentRequestDTO commentRequestDTO, Long questionId);

    void deleteComment(Long commentId);

    void commentOnAnswer(CommentRequestDTO commentRequestDTO, Long answerId);

    void commentOnAnswerComment(CommentRequestDTO commentRequestDTO, Long answerId, Long commentId);


}
