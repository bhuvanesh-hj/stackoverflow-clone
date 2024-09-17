package com.stackoverflow.repository;

import com.stackoverflow.entity.Comment;
import com.stackoverflow.service.AnswerService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {

}
