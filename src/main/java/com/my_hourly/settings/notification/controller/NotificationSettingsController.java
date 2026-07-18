package com.my_hourly.settings.notification.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.settings.notification.dto.request.NotificationSettingsRequest;
import com.my_hourly.settings.notification.dto.response.NotificationSettingsResponse;
import com.my_hourly.settings.notification.service.NotificationSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/settings/notifications")
@RequiredArgsConstructor
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<NotificationSettingsResponse>> getNotificationSettings() {

        NotificationSettingsResponse response =
                notificationSettingsService.getNotificationSettings();

        ApiResponse<NotificationSettingsResponse> apiResponse =
                ApiResponse.<NotificationSettingsResponse>builder()
                        .success(true)
                        .message("Notification settings fetched successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<NotificationSettingsResponse>> updateNotificationSettings(
            @Valid @RequestBody NotificationSettingsRequest request) {

        NotificationSettingsResponse response =
                notificationSettingsService.updateNotificationSettings(request);

        ApiResponse<NotificationSettingsResponse> apiResponse =
                ApiResponse.<NotificationSettingsResponse>builder()
                        .success(true)
                        .message("Notification settings updated successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }
}