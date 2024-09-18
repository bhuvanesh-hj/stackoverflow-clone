package com.stackoverflow.service;

import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.dto.user.UserRegistrationDTO;
import com.stackoverflow.dto.user.UserUpdateDTO;
import com.stackoverflow.entity.User;

import java.util.List;

public interface UserService {

    public List<UserDetailsDTO> getAllUsers();

    public UserDetailsDTO getUserById(Long userId);

    public UserDetailsDTO getUserByEmail(String email);

    public UserDetailsDTO getUserByUsername(String username);

    public UserDetailsDTO createUser(UserRegistrationDTO user);

    public UserDetailsDTO updateUser(Long userId, UserUpdateDTO updatedUserDetails);

    public Boolean updatePassword(Long userId, String oldPassword, String newPassword);

    public Boolean deleteUser(Long userId);

    public User getLoggedInUser();

    public Boolean isUserLoggedIn();
}
