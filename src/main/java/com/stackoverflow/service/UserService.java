package com.stackoverflow.service;

import com.stackoverflow.dto.users.UserDetailsDTO;
import com.stackoverflow.dto.users.UserRegistrationDTO;
import com.stackoverflow.dto.users.UserUpdateDTO;
import com.stackoverflow.dto.users.UserViewDTO;
import com.stackoverflow.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    List<UserDetailsDTO> getAllUsers();

    UserDetailsDTO getUserById(Long userId);

    UserDetailsDTO getUserByEmail(String email);

    UserDetailsDTO getUserByUsername(String username);

    UserDetailsDTO createUser(UserRegistrationDTO user);

    UserDetailsDTO updateUser(Long userId, UserUpdateDTO updatedUserDetails);

    Boolean updateUserPassword(Long userId, String oldPassword, String newPassword);

    Boolean deleteUserById(Long userId);

    User getLoggedInUser();

    Page<UserViewDTO> getAllUsersWithCounts(int page, int size, String searchQuery);

    Boolean isUserLoggedIn();

    User getLoggedInUserOrNull();

}
