package com.example.api.user.service;

import com.example.api.user.dto.request.ResetPasswordForm;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.user.model.PasswordResetToken;
import com.example.api.user.model.User;
import com.example.api.user.repository.PasswordResetTokenRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.util.service.email.EmailService;
import com.example.api.validator.PasswordResetValidator;
import com.example.api.validator.PasswordValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {
    private final int TOKEN_SIZE = 8;
    private final int TOKEN_TTL_IN_MIN = 5;

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetValidator passwordResetValidator;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordValidator passwordValidator;

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);

        String plainToken = UUID.randomUUID()
                .toString()
                .replaceAll("_", "")
                .substring(0, TOKEN_SIZE);
        String hashedToken = passwordEncoder.encode(plainToken);

        PasswordResetToken token = new PasswordResetToken();
        token.setIsUsed(false);
        token.setHashedToken(hashedToken);
        token.setCreationTime(System.currentTimeMillis());
        token.setExpirationTime(token.getCreationTime() + (TOKEN_TTL_IN_MIN * 60 * 1_000));
        token.setUser(user);
        passwordResetTokenRepository.save(token);
        user.setPasswordResetToken(token);
        userRepository.save(user);

        log.info("Sending password reset email to {}", email);
        emailService.sendEmail(user, "Systematic Chaos - password reset", getPasswordResetEmailMessage(plainToken));
    }

    public void resetPassword(ResetPasswordForm form) throws RequestValidationException {
        log.info("Trying to reset password for {}", form.getEmail());
        String email = form.getEmail();
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);

        PasswordResetToken token = user.getPasswordResetToken();
        passwordResetValidator.validateTokenIsNotNull(email, token);
        passwordResetValidator.validateTokenIsNotUsed(email, token);
        passwordResetValidator.validateExpirationTime(email, System.currentTimeMillis(), token.getExpirationTime());
        passwordResetValidator.validateToken(email, passwordEncoder, form.getPasswordResetToken(), token.getHashedToken());
        passwordValidator.validatePassword(form.getNewPassword());
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        token.setIsUsed(true);
    }

    private String getPasswordResetEmailMessage(String token) {
        return String.format("Hello,\n" +
                "\n" +
                "Systematic Chaos website administration here. You are receiving this email because it was provided for password reset on our website.\n" +
                "\n" +
                "Your password change token is: %s\n" +
                "\n" +
                "If you have not provided your e-mail address or do not know why you received this message - ignore it.\n" +
                "\n" +
                "The message was generated automatically, do not reply to it.\n" +
                "\n" +
                "Best regards,\n" +
                "Systematic Chaos Administration", token);
    }
}
