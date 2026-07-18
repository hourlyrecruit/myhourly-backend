package com.my_hourly.settings.workLogs.service;

import com.my_hourly.settings.workLogs.dto.request.WorkLogSettingsRequest;
import com.my_hourly.settings.workLogs.dto.response.WorkLogSettingsResponse;
import com.my_hourly.settings.workLogs.entity.WorkLogSettings;

public interface WorkLogSettingsService {

    WorkLogSettings getSettings();

    WorkLogSettingsResponse getWorkLogSettings();

    WorkLogSettingsResponse updateWorkLogSettings(
            WorkLogSettingsRequest request);

}