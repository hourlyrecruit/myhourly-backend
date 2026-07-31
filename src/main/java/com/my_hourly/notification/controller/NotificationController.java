package com.my_hourly.notification.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.notification.api.request.AnnouncementRequest;
import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(name = "14-Announcement & Celebration posts & Notification Management", description = "APIs for managing employee notifications")
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

    @Operation(summary = "Create an announcement. Access: 'MANAGER','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','MANAGER')")
    @PostMapping(value = "/announcement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE),
                            @Encoding(name = "attachments", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    }
            )
    )
    public ApiResponse<Void> createAnnouncement(
            @Valid @RequestPart("request") AnnouncementRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {

        notificationService.createAnnouncement(request, attachments);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Announcement sent successfully.")
                .build();
    }
}