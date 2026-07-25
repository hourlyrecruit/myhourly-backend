package com.my_hourly.notification.service;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.notification.api.request.AnnouncementRequest;
import com.my_hourly.notification.api.response.NotificationResponse;

public interface NotificationService {

    /**
     * Returns logged-in employee notifications.
     */
    PageResponse<NotificationResponse> getMyNotifications(
            int page,
            int size
    );

    /**
     * Returns unread notification count.
     */
    long getUnreadCount();

    /**
     * Mark a notification as read.
     */
    void markAsRead(
            Long notificationId
    );

    /**
     * Mark all notifications as read.
     */
    void markAllAsRead();

    /**
     * HR Announcement.
     */
    void createAnnouncement(
            AnnouncementRequest request
    );

    /**
     * Internal notification creation.
     */
    void createNotification(
            com.my_hourly.employee.entity.Employee employee,
            String title,
            String message,
            com.my_hourly.notification.enums.NotificationType notificationType,
            com.my_hourly.notification.enums.NotificationPriority priority,
            com.my_hourly.notification.enums.ReferenceType referenceType,
            Long referenceId
    );

    /**
     * Scheduler Methods
     */
    void processAttendanceNotifications();

    void processLeaveNotifications();

    void processBirthdayNotifications();

    void processWorkAnniversaryNotifications();

    void processHolidayNotifications();

}