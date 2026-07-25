package com.my_hourly.notification.enums;

public enum NotificationType {

    // Leave Notifications
    LEAVE_APPLIED,
    LEAVE_MANAGER_APPROVED,
    LEAVE_HR_APPROVED,
    LEAVE_REJECTED,
    LEAVE_CANCELLED,

    // Attendance Notifications
    LATE_CHECK_IN,
    ABSENT,
    MISSED_CHECKOUT,

    // Calendar Notifications
    HOLIDAY,
    BIRTHDAY,
    WORK_ANNIVERSARY,

    // HR Notifications
    ANNOUNCEMENT,

    // Generic Notification
    GENERAL
}