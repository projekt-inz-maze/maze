package com.example.api.security;

import com.example.api.user.model.User;
import com.example.api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j

public class AuthenticationService {
    UserRepository userRepository;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public User getCurrentUser() {
        String email = getAuthentication().getName();
        log.info("Fetching loged in user {}", email);
        return Optional.ofNullable(userRepository.findUserByEmail(email)).orElseThrow();
    }
}
