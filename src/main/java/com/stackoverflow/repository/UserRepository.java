package com.stackoverflow.repository;

import com.stackoverflow.dto.users.UserViewDTO;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    @Query("SELECT DISTINCT new com.stackoverflow.dto.users.UserViewDTO(u.id, u.firstName, u.lastName, COUNT(DISTINCT q.id), COUNT(DISTINCT a.id), u.profilePicture,u.reputations) " +
            "FROM User u " +
            "LEFT JOIN u.questions q " +
            "LEFT JOIN u.answers a " +
            "WHERE (:searchTerm IS NULL OR :searchTerm = '' " +
            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')))" +
            "GROUP BY u.id")
    Page<UserViewDTO> findAllUsersWithQuestionAndAnswerCount(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<Question> findQuestionsSavedById(Long userId);



}
