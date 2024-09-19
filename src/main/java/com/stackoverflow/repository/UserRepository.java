package com.stackoverflow.repository;

import com.stackoverflow.dto.user.UserViewDTO;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    @Query("SELECT new com.stackoverflow.dto.user.UserViewDTO(u.firstName, u.lastName, COUNT(DISTINCT q.id), COUNT(DISTINCT a.id)) " +
            "FROM User u " +
            "LEFT JOIN u.questions q " +
            "LEFT JOIN u.answers a " +
            "GROUP BY u.firstName, u.lastName")
    List<UserViewDTO> findAllUsersWithQuestionAndAnswerCount();

    List<Question> findQuestionsSavedById(Long userId);
}
