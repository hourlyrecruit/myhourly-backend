package com.my_hourly.authentication.service.impl;

import com.my_hourly.authentication.service.PasswordResetEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetEmailServiceImpl implements PasswordResetEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.password-reset.frontend-url}")
    private String resetPageUrl;

    @Value("${app.password-reset.from}")
    private String from;

    @Override
    @Async
    public void sendResetLink(String recipientEmail, String resetToken) {
        try {
            String resetUrl = UriComponentsBuilder.fromUriString(resetPageUrl)
                    .queryParam("token", resetToken)
                    .build()
                    .encode()
                    .toUriString();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(recipientEmail);
            message.setSubject("Reset your MyHourly password");
            message.setText("We received a request to reset your MyHourly password. "
                    + "Use the following link within 30 minutes:\n\n" + resetUrl
                    + "\n\nIf you did not request this, you can safely ignore this email.");
            mailSender.send(message);
            log.info("Password reset email sent to {}", recipientEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", recipientEmail, e.getMessage(), e);
        }
    }
}
