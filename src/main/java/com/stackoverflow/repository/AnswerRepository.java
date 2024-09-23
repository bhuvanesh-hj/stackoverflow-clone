package com.stackoverflow.repository;

import com.stackoverflow.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByAuthorId(Long userId);

    Integer countByQuestionId(Long questionId);

    @Query("SELECT CASE " +
            "WHEN av.isUpvote = true THEN 1 " +
            "WHEN av.isUpvote = false THEN 0 " +
            "ELSE -1 " +
            "END " +
            "FROM AnswerVote av " +
            "WHERE av.answer.id = :answerId AND av.user.id = :userId")
    Integer getUserVoteStatus(@Param("answerId") Long answerId, @Param("userId") Long userId);

}
