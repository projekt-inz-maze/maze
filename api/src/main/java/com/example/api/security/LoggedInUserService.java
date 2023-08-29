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

public class LoggedInUserService {
    UserRepository userRepository;
    AuthenticationService authenticationService;

    public User getCurrentUser() {
        String email = authenticationService.getAuthentication().getName();
        log.info("Fetching logged in user {}", email);
        return Optional.ofNullable(userRepository.findUserByEmail(email)).orElseThrow();
    }
}
