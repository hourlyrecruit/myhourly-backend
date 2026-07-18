package com.my_hourly.settings.company.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.settings.company.dto.request.CompanySettingsRequest;
import com.my_hourly.settings.company.dto.response.CompanySettingsResponse;
import com.my_hourly.settings.company.entity.CompanySettings;
import com.my_hourly.settings.company.mapper.CompanySettingsMapper;
import com.my_hourly.settings.company.repository.CompanySettingsRepository;
import com.my_hourly.settings.company.service.CompanySettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanySettingsServiceImpl implements CompanySettingsService {

    private final CompanySettingsRepository repository;
    private final CompanySettingsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CompanySettingsResponse getCompanySettings() {

        CompanySettings settings = repository.findFirstByActiveTrue()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company settings not found.", ErrorCode.COMPANY_NOT_FOUND));

        return mapper.toResponse(settings);
    }

    @Override
    public CompanySettingsResponse updateCompanySettings(
            CompanySettingsRequest request) {

        CompanySettings settings = repository.findFirstByActiveTrue()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company settings not found.", ErrorCode.COMPANY_NOT_FOUND));

        mapper.updateEntity(request, settings);

        CompanySettings updated = repository.save(settings);

        return mapper.toResponse(updated);
    }

}
