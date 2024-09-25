package com.stackoverflow.repository;

import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT a FROM Answer a LEFT JOIN FETCH a.question WHERE a.id = :answerId")
    Optional<Answer> findByIdWithQuestion(@Param("answerId") Long answerId);

    Page<Answer> findAllByQuestionId(Long questionId, Pageable pageable);

    @Query("SELECT a FROM Answer a LEFT JOIN a.answerVote v " +
            "WHERE a.question.id = :questionId " +
            "GROUP BY a.id " +
            "ORDER BY COUNT(CASE WHEN v.isUpvote = true THEN 1 END) DESC")
    Page<Answer> findAllAnswersOrderedByUpVotes(Pageable pageable, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.author.id = :userId")
    Integer getAnswersCount(@Param("userId") Long userId);
}
