package com.my_hourly.settings.notification.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsResponse {

    private Long id;

    private Boolean emailNotificationsEnabled;

    private Boolean inAppNotificationsEnabled;

    private Boolean attendanceNotificationsEnabled;

    private Boolean leaveNotificationsEnabled;

    private Boolean workLogNotificationsEnabled;

    private Boolean holidayNotificationsEnabled;

    private Boolean birthdayNotificationsEnabled;

    private Boolean announcementNotificationsEnabled;

    private Boolean notifyManagers;

    private Boolean notifyEmployees;

    private Boolean active;

}
