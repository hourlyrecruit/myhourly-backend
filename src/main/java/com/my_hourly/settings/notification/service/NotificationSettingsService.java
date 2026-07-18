package com.my_hourly.settings.notification.service;


import com.my_hourly.settings.notification.dto.request.NotificationSettingsRequest;
import com.my_hourly.settings.notification.dto.response.NotificationSettingsResponse;
import com.my_hourly.settings.notification.entity.NotificationSettings;

public interface NotificationSettingsService {

    NotificationSettings getSettings();

    NotificationSettingsResponse getNotificationSettings();

    NotificationSettingsResponse updateNotificationSettings(
            NotificationSettingsRequest request);

}