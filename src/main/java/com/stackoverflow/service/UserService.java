package com.stackoverflow.service;

import com.stackoverflow.entity.User;

import java.util.List;

public interface UserService {

    public List<User> getAllUsers();

    public User getUserById(Long userId);

    public User getUserByEmail(String email);

    public User getUserByUsername(String username);

    public User createUser(User user);

    public User updateUser(Long userId, User userDetails);

    void deleteUser(Long userId);
}
