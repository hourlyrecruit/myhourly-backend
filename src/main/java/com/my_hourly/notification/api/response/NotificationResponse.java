package com.my_hourly.notification.api.response;

import com.my_hourly.notification.enums.NotificationPriority;
import com.my_hourly.notification.enums.NotificationType;
import com.my_hourly.notification.enums.ReferenceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;

    private String title;

    private String message;

    private NotificationType notificationType;

    private NotificationPriority priority;

    private ReferenceType referenceType;

    private Long referenceId;

    private Boolean isRead;

    private LocalDateTime createdAt;
}