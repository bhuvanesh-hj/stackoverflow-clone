package com.stackoverflow.security;

import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StackoverflowUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public StackoverflowUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found by username")
                );
        return user;
    }
}
