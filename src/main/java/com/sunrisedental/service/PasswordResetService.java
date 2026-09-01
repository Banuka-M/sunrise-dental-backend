package com.sunrisedental.service;

import com.sunrisedental.model.PasswordResetToken;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.PasswordResetTokenRepository;
import com.sunrisedental.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    @Transactional
    public void forgotPassword(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No account found with this email."
                        )
                );

        String token =
                UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(token);
        resetToken.setUser(user);

        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(30)
        );

        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:5173/reset-password?token="
                        + token;

        String subject =
                "Sunrise Dental - Password Reset";

        String message =
                "Hello "
                        + user.getFullName()
                        + ",\n\n"
                        + "We received a request to reset your "
                        + "Sunrise Dental account password.\n\n"
                        + "Click the link below to reset your password:\n\n"
                        + resetLink
                        + "\n\n"
                        + "This link will expire in 30 minutes.\n\n"
                        + "If you did not request a password reset, "
                        + "please ignore this email.\n\n"
                        + "Sunrise Dental";

        emailService.sendEmail(
                user,
                subject,
                message
        );
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Transactional
    public void resetPassword(
            String token,
            String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password reset token."
                                )
                        );

        if (resetToken.isUsed()) {

            throw new IllegalArgumentException(
                    "This password reset link has already been used."
            );
        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "This password reset link has expired."
            );
        }

        User user =
                resetToken.getUser();

        user.setPasswordHash(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        resetToken.setUsed(true);

        tokenRepository.save(resetToken);
    }
}
