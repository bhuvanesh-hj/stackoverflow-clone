package com.stackoverflow.service;

import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.entity.Comment;

public interface CommentService {

    String createNestedComment(CommentRequestDTO commentRequestDTO, Long questionId);

    String createNestedComment(CommentRequestDTO commentRequestDTO, Long questionId, Long commentId);

    Comment getCommentById(Long commentId);

    void updateComment(Long commentId, CommentRequestDTO commentRequestDTO, Long questionId);

    void deleteComment(Long commentId);

}
