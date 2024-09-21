package com.stackoverflow.service;

import com.stackoverflow.dto.CommentRequestDTO;
import com.stackoverflow.entity.Comment;

public interface CommentService {

    public String createComment(CommentRequestDTO commentRequestDTO, Long questionId);

    public String createComment(CommentRequestDTO commentRequestDTO, Long questionId, Long commentId);

    public Comment getCommentById(Long commentId);

    public void update(Long commentId, CommentRequestDTO commentRequestDTO, Long questionId);

    public void delete(Long commentId);


}
