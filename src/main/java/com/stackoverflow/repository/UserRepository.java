package com.stackoverflow.repository;

import com.stackoverflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

//    boolean checkEmailAlreadyExists(String email);
//
//    boolean checkUsernameAlreadyExists(String username);

    Optional<User> findByEmailOrUsername(String email, String username);
}
