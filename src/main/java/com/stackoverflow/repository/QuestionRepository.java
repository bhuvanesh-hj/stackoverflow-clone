package com.stackoverflow.repository;

import com.stackoverflow.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAll(Pageable pageable);

    List<Question> findByAuthorId(Long userId);

    @Query("SELECT DISTINCT q FROM Question q " +
            "LEFT JOIN q.tags t " +
            "LEFT JOIN q.author u " +
            "LEFT JOIN q.answers a " +
            "WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(q.body) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.body) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> getSearchQuestions(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT CASE " +
            "WHEN qv.isUpvote = true THEN 1 " +
            "WHEN qv.isUpvote = false THEN 0 " +
            "ELSE -1 " +
            "END " +
            "FROM QuestionVote qv " +
            "WHERE qv.question.id = :questionId AND qv.user.id = :userId")
    Integer getUserVoteStatus(@Param("questionId") Long questionId, @Param("userId") Long userId);

    List<Question> findBySavedByUsers_Id(Long userId);

    List<Question> findByAnswers_AuthorId(Long id);

    @Query("SELECT q FROM Question q LEFT JOIN q.questionVotes v GROUP BY q.id ORDER BY COUNT(CASE WHEN v.isUpvote = true THEN 1 END) DESC")
    Page<Question> findAllQuestionsOrderedByUpvotes(Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t IS NULL OR t.name IN :tags AND q.id != :questionId GROUP BY q.id ORDER BY COUNT(t) DESC")
    List<Question> findRelatedQuestionsByTags(@Param("tags") List<String> tags, @Param("questionId") Long questionId, Pageable pageable);

}
