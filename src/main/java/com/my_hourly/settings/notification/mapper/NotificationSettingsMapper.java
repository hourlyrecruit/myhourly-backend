package com.my_hourly.settings.notification.mapper;


import com.my_hourly.settings.notification.dto.request.NotificationSettingsRequest;
import com.my_hourly.settings.notification.dto.response.NotificationSettingsResponse;
import com.my_hourly.settings.notification.entity.NotificationSettings;
import org.springframework.stereotype.Component;

@Component
public class NotificationSettingsMapper {

    public NotificationSettingsResponse toResponse(NotificationSettings entity) {

        if (entity == null) {
            return null;
        }

        return NotificationSettingsResponse.builder()
                .id(entity.getId())
                .emailNotificationsEnabled(entity.getEmailNotificationsEnabled())
                .inAppNotificationsEnabled(entity.getInAppNotificationsEnabled())
                .attendanceNotificationsEnabled(entity.getAttendanceNotificationsEnabled())
                .leaveNotificationsEnabled(entity.getLeaveNotificationsEnabled())
                .workLogNotificationsEnabled(entity.getWorkLogNotificationsEnabled())
                .holidayNotificationsEnabled(entity.getHolidayNotificationsEnabled())
                .birthdayNotificationsEnabled(entity.getBirthdayNotificationsEnabled())
                .announcementNotificationsEnabled(entity.getAnnouncementNotificationsEnabled())
                .notifyManagers(entity.getNotifyManagers())
                .notifyEmployees(entity.getNotifyEmployees())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(
            NotificationSettingsRequest request,
            NotificationSettings entity) {

        entity.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        entity.setInAppNotificationsEnabled(request.getInAppNotificationsEnabled());
        entity.setAttendanceNotificationsEnabled(request.getAttendanceNotificationsEnabled());
        entity.setLeaveNotificationsEnabled(request.getLeaveNotificationsEnabled());
        entity.setWorkLogNotificationsEnabled(request.getWorkLogNotificationsEnabled());
        entity.setHolidayNotificationsEnabled(request.getHolidayNotificationsEnabled());
        entity.setBirthdayNotificationsEnabled(request.getBirthdayNotificationsEnabled());
        entity.setAnnouncementNotificationsEnabled(request.getAnnouncementNotificationsEnabled());
        entity.setNotifyManagers(request.getNotifyManagers());
        entity.setNotifyEmployees(request.getNotifyEmployees());
    }
}