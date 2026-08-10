package com.my_hourly.authentication.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(
            String email,
            String username,
            String password
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Welcome to MyHourly - Login Credentials");

        message.setText(
                "Dear " + username + ",\n\n" +

                        "Welcome to MyHourly!\n\n" +

                        "Your account has been successfully created.\n\n" +

                        "Your login credentials are:\n\n" +
                        "Username: " + username + "\n" +
                        "Email: " + email + "\n" +
                        "Password: " + password + "\n\n" +

                        "Please use these credentials to log in to your account.\n" +
                        "After your first login, please change your password for security.\n\n" +

                        "Regards,\n" +
                        "MyHourly Team"
        );

        mailSender.send(message);
    }
}