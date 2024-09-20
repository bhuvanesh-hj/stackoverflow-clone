package com.stackoverflow.repository;

import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT DISTINCT t " +
            "FROM Tag t " +
            "WHERE (:searchTerm IS NULL OR :searchTerm = '' " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Tag> findAllTagsWithQuestionCount(String searchTerm, Pageable pageable);

    Optional<Tag> findByName(String tagName);
}
