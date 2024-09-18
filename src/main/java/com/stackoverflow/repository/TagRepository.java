package com.stackoverflow.repository;

import com.stackoverflow.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t, COUNT(q.id) as questionCount FROM Tag t LEFT JOIN t.questions q GROUP BY t")
    List<Object[]> findAllTagsWithQuestionCount();

    Optional<Tag> findByName(String tagName);
}
