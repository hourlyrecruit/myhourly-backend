package com.my_hourly.settings.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsRequest {

    @NotNull
    private Boolean emailNotificationsEnabled;

    @NotNull
    private Boolean inAppNotificationsEnabled;

    @NotNull
    private Boolean attendanceNotificationsEnabled;

    @NotNull
    private Boolean leaveNotificationsEnabled;

    @NotNull
    private Boolean workLogNotificationsEnabled;

    @NotNull
    private Boolean holidayNotificationsEnabled;

    @NotNull
    private Boolean birthdayNotificationsEnabled;

    @NotNull
    private Boolean announcementNotificationsEnabled;

    @NotNull
    private Boolean notifyManagers;

    @NotNull
    private Boolean notifyEmployees;

}
