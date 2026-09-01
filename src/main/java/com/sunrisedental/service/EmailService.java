package com.sunrisedental.service;

import com.sunrisedental.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // =========================================================
    // SEND EMAIL
    // =========================================================

    public void sendEmail(
            String to,
            String subject,
            String message) {

        if (to == null || to.isBlank()) {
            return;
        }

        SimpleMailMessage email =
                new SimpleMailMessage();

        email.setFrom(fromEmail);
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    // =========================================================
    // SEND EMAIL TO USER
    // =========================================================

    public void sendEmail(
            User user,
            String subject,
            String message) {

        if (user == null) {
            return;
        }

        sendEmail(
                user.getEmail(),
                subject,
                message
        );
    }
}
