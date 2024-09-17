package com.stackoverflow.service;

import com.stackoverflow.dto.CommentRequestDTO;
import com.stackoverflow.entity.Comment;

public interface CommentService {

    public void createComment(CommentRequestDTO commentRequestDTO, Long questionId, Long answerId);

    public Comment getCommentById(Long commentId);

    public void update(Long commentId, CommentRequestDTO commentRequestDTO, Long questionId, Long answerId);

    public void delete(Long commentId);


}
