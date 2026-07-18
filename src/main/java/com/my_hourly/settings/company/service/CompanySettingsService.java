package com.my_hourly.settings.company.service;

import com.my_hourly.settings.company.dto.request.CompanySettingsRequest;
import com.my_hourly.settings.company.dto.response.CompanySettingsResponse;

public interface CompanySettingsService {

    CompanySettingsResponse getCompanySettings();

    CompanySettingsResponse updateCompanySettings(
            CompanySettingsRequest request);

}