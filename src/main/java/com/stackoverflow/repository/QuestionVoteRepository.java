package com.stackoverflow.repository;

import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.QuestionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionVoteRepository extends JpaRepository<QuestionVote, Long> {
    Optional<QuestionVote> findByQuestionIdAndUserId(Long questionId, Long userId);

    int countByQuestionIdAndIsUpvoteTrue(Long questionId);

    int countByQuestionIdAndIsUpvoteFalse(Long questionId);

}