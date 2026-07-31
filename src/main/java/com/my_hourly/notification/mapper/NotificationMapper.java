package com.my_hourly.notification.mapper;

import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return toResponse(notification, null);
    }

    public NotificationResponse toResponse(Notification notification, List<String> attachmentUrls) {

        if (notification == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .priority(notification.getPriority())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .attachmentUrls(attachmentUrls)
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}