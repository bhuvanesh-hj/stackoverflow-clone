package com.stackoverflow.repository;

import com.stackoverflow.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

//    @Query("SELECT q FROM Question q " +
//            "JOIN q.tags t " +
//            "JOIN q.author u " +
//            "WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(q.body) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<Question> searchQuestions(@Param("keyword") String keyword);
}
