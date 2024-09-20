package com.stackoverflow.repository;

import com.stackoverflow.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAll(Pageable pageable);

    List<Question> findByAuthorId(Long userId);

//    @Query("SELECT q FROM Question q " +
//            "JOIN q.tags t " +
//            "JOIN q.author u " +
//            "WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(q.body) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<Question> searchQuestions(@Param("keyword") String keyword);

    @Query("SELECT CASE " +
            "WHEN qv.isUpvote = true THEN 1 " +
            "WHEN qv.isUpvote = false THEN 0 " +
            "ELSE -1 " +
            "END " +
            "FROM QuestionVote qv " +
            "WHERE qv.question.id = :questionId AND qv.user.id = :userId")
    Integer getUserVoteStatus(@Param("questionId") Long questionId, @Param("userId") Long userId);
}
