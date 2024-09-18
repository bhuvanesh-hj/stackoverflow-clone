package com.stackoverflow.service.impl;

import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.dto.user.UserRegistrationDTO;
import com.stackoverflow.dto.user.UserUpdateDTO;
import com.stackoverflow.entity.Role;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceAlreadyExistsException;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.repository.RoleRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDetailsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No such user exists"));
        return modelMapper.map(user, UserDetailsDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No such user exists"));
        return modelMapper.map(user, UserDetailsDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No such user exists"));
        return modelMapper.map(user, UserDetailsDTO.class);
    }

    @Override
    @Transactional
    public UserDetailsDTO createUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> existingUser = userRepository.findByEmailOrUsername(userRegistrationDTO.getEmail(), userRegistrationDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    existingUser.get().getEmail().equals(userRegistrationDTO.getEmail()) ? "Email is already in use." : "Username is already in use."
            );
        }

        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));

        String defaultRole = "ROLE_USER";
        Optional<Role> role = roleRepository.findByName(defaultRole);
        System.out.println("role = " + role);

        if (role.isEmpty()) {
            throw new ResourceNotFoundException("No such role exists");
        } else {
            user.addRole(role.get());
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email is already in use.");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username is already in use.");
        }

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDetailsDTO.class);
    }

    @Override
    @Transactional
    public UserDetailsDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No such user exists"));

        user.setEmail(userUpdateDTO.getEmail());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserDetailsDTO.class);
    }

    @Override
    @Transactional
    public Boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No such user exists"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("No such user exists");
        }
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not logged in ");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElse(null);
    }

    public Boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (userRepository.findByUsername(authentication.getName()).isPresent()) {
            return true;
        }
        return false;
    }
}
