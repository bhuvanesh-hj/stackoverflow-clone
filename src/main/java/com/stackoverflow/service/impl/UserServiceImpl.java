package com.stackoverflow.service.impl;

import com.stackoverflow.dto.users.UserDetailsDTO;
import com.stackoverflow.dto.users.UserRegistrationDTO;
import com.stackoverflow.dto.users.UserUpdateDTO;
import com.stackoverflow.dto.users.UserViewDTO;
import com.stackoverflow.entity.Role;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceAlreadyExistsException;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.exception.UserBountieException;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.RoleRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
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
        user.setReputations(10);

        String defaultRole = "ROLE_USER";
        Optional<Role> role = roleRepository.findByName(defaultRole);

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User updatingUser = (User) authentication.getPrincipal();

        user.setEmail(userUpdateDTO.getEmail());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setUsername(userUpdateDTO.getUsername());
        updatingUser.setUsername(userUpdateDTO.getUsername());
        user.setLastName(userUpdateDTO.getLastName());
        user.setProfilePicture(userUpdateDTO.getProfilePicture());

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserDetailsDTO.class);
    }

    @Override
    @Transactional
    public Boolean updateUserPassword(Long userId, String oldPassword, String newPassword) {
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
    public Boolean deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("No such user exists");
        }
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public User getLoggedInUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not logged in ");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElse(null);
    }

    @Override
    public Boolean isBountied(Long userId) {
        User user = getLoggedInUser();
        if (user.getReputations() >= 30) {
            return true;
        }
        throw new UserBountieException("You don't have enough bounties to complete this action.");
    }

    public Boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUsername(authentication.getName()).isPresent();
    }

    @Override
    public Page<UserViewDTO> getAllUsersWithCounts(int page, int size, String searchQuery) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllUsersWithQuestionAndAnswerCount(searchQuery, pageable);
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UserNotAuthenticatedException("User not logged in");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotAuthenticatedException("User not found by username"));
    }

}