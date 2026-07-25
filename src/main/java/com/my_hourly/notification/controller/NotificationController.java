package com.my_hourly.notification.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.notification.api.request.AnnouncementRequest;
import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Management", description = "APIs for managing employee notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get logged-in employee notifications, Access: EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ApiResponse.<PageResponse<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully.")
                .data(notificationService.getMyNotifications(page, size))
                .build();
    }

    @Operation(summary = "Get unread notification count. Access: EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {

        return ApiResponse.<Long>builder()
                .success(true)
                .message("Unread notification count fetched successfully.")
                .data(notificationService.getUnreadCount())
                .build();
    }

    @Operation(summary = "Mark a notification as read. Access: EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long notificationId
    ) {

        notificationService.markAsRead(notificationId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification marked as read successfully.")
                .build();
    }

    @Operation(summary = "Mark all notifications as read. Access: EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ApiResponse.<Void>builder()
                .success(true)
                .message("All notifications marked as read successfully.")
                .build();
    }

    @Operation(summary = "Create an announcement. Access: EMPLOYEE','MANAGER','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    @PostMapping("/announcement")
    public ApiResponse<Void> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest request
    ) {

        notificationService.createAnnouncement(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Announcement sent successfully.")
                .build();
    }
}