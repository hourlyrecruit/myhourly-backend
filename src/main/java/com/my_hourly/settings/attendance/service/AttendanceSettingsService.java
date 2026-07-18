package com.my_hourly.settings.attendance.service;

import com.my_hourly.settings.attendance.dto.request.AttendanceSettingsRequest;
import com.my_hourly.settings.attendance.dto.response.AttendanceSettingsResponse;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;

public interface AttendanceSettingsService {

    AttendanceSettings getSettings();

    AttendanceSettingsResponse getAttendanceSettings();

    AttendanceSettingsResponse updateAttendanceSettings(
            AttendanceSettingsRequest request);

}
