package com.stackoverflow.repository;

import com.stackoverflow.entity.QuestionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionVoteRepository extends JpaRepository<QuestionVote, Long> {

    Optional<QuestionVote> findByQuestionIdAndUserId(Long questionId, Long userId);

    Integer countByQuestionIdAndIsUpvoteTrue(Long questionId);

    Integer countByQuestionIdAndIsUpvoteFalse(Long questionId);

}