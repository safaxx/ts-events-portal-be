package com.techsisters.gatherly.service;

import com.techsisters.gatherly.config.CustomUserDetails;
import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
/**
 * Implementation of Spring Security's UserDetailsService.
 * Loads user data from the database and wraps it in CustomUserDetails.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + username));

        log.debug("User found: {}", user.getEmail());

        return new CustomUserDetails(user);

    }
}
