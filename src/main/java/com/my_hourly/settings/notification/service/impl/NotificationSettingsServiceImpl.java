package com.my_hourly.settings.notification.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.settings.notification.dto.request.NotificationSettingsRequest;
import com.my_hourly.settings.notification.dto.response.NotificationSettingsResponse;
import com.my_hourly.settings.notification.entity.NotificationSettings;
import com.my_hourly.settings.notification.mapper.NotificationSettingsMapper;
import com.my_hourly.settings.notification.repository.NotificationSettingsRepository;
import com.my_hourly.settings.notification.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationSettingsMapper notificationSettingsMapper;

    @Override
    @Transactional(readOnly = true)
    public NotificationSettings getSettings() {

        return notificationSettingsRepository.findFirstByActiveTrue()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification settings not found.", ErrorCode.VALIDATION_FAILED));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationSettingsResponse getNotificationSettings() {

        return notificationSettingsMapper.toResponse(getSettings());
    }

    @Override
    public NotificationSettingsResponse updateNotificationSettings(
            NotificationSettingsRequest request) {

        validateNotificationSettings(request);

        NotificationSettings settings = getSettings();

        notificationSettingsMapper.updateEntity(request, settings);

        NotificationSettings updated =
                notificationSettingsRepository.save(settings);

        return notificationSettingsMapper.toResponse(updated);
    }

    private void validateNotificationSettings(
            NotificationSettingsRequest request) {

        validateNotificationChannels(request);
        validateNotificationRecipients(request);
        validateNotificationTypes(request);
    }

    private void validateNotificationChannels(
            NotificationSettingsRequest request) {

        if (!request.getEmailNotificationsEnabled()
                && !request.getInAppNotificationsEnabled()) {

            throw new BadRequestException(
                    "At least one notification channel must be enabled.", ErrorCode.VALIDATION_FAILED);
        }
    }

    private void validateNotificationRecipients(
            NotificationSettingsRequest request) {

        if (!request.getNotifyManagers()
                && !request.getNotifyEmployees()) {

            throw new BadRequestException(
                    "At least one notification recipient must be enabled.", ErrorCode.VALIDATION_FAILED);
        }
    }

    private void validateNotificationTypes(
            NotificationSettingsRequest request) {

        if (!request.getAttendanceNotificationsEnabled()
                && !request.getLeaveNotificationsEnabled()
                && !request.getWorkLogNotificationsEnabled()
                && !request.getHolidayNotificationsEnabled()
                && !request.getBirthdayNotificationsEnabled()
                && !request.getAnnouncementNotificationsEnabled()) {

            throw new BadRequestException(
                    "At least one notification type must be enabled.", ErrorCode.VALIDATION_FAILED);
        }
    }
}
