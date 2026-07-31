package com.my_hourly.authentication.service;

public interface PasswordResetEmailService {

    void sendResetLink(String recipientEmail, String resetToken);
}
