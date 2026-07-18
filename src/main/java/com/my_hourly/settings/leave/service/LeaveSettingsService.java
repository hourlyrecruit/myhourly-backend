package com.my_hourly.settings.leave.service;

import com.my_hourly.settings.leave.dto.request.LeaveSettingsRequest;
import com.my_hourly.settings.leave.dto.response.LeaveSettingsResponse;
import com.my_hourly.settings.leave.entity.LeaveSettings;

public interface LeaveSettingsService {

    LeaveSettings getSettings();

    LeaveSettingsResponse getLeaveSettings();

    LeaveSettingsResponse updateLeaveSettings(
            LeaveSettingsRequest request);
}