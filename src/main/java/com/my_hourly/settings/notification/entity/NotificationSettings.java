package com.my_hourly.settings.notification.entity;

import com.my_hourly.settings.BaseSettings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings extends BaseSettings {

    @Column(nullable = false)
    private Boolean emailNotificationsEnabled;

    @Column(nullable = false)
    private Boolean inAppNotificationsEnabled;

    @Column(nullable = false)
    private Boolean attendanceNotificationsEnabled;

    @Column(nullable = false)
    private Boolean leaveNotificationsEnabled;

    @Column(nullable = false)
    private Boolean workLogNotificationsEnabled;

    @Column(nullable = false)
    private Boolean holidayNotificationsEnabled;

    @Column(nullable = false)
    private Boolean birthdayNotificationsEnabled;

    @Column(nullable = false)
    private Boolean announcementNotificationsEnabled;

    @Column(nullable = false)
    private Boolean notifyManagers;

    @Column(nullable = false)
    private Boolean notifyEmployees;

    @Column(nullable = false)
    private Boolean active;
}
