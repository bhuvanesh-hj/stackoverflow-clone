package com.stackoverflow.repository;

import com.stackoverflow.entity.AnswerVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerVoteRepository extends JpaRepository<AnswerVote,Long> {
    Optional<AnswerVote> findByAnswerIdAndUserId(Long answerId, Long userId);

    int countByAnswerIdAndIsUpvote(Long answerId, boolean isUpvote);
}
