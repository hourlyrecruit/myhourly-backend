package com.my_hourly.settings.workLogs.service.impl;


import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.settings.workLogs.dto.request.WorkLogSettingsRequest;
import com.my_hourly.settings.workLogs.dto.response.WorkLogSettingsResponse;
import com.my_hourly.settings.workLogs.entity.WorkLogSettings;
import com.my_hourly.settings.workLogs.mapper.WorkLogSettingsMapper;
import com.my_hourly.settings.workLogs.repository.WorkLogSettingsRepository;
import com.my_hourly.settings.workLogs.service.WorkLogSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkLogSettingsServiceImpl implements WorkLogSettingsService {

    private final WorkLogSettingsRepository workLogSettingsRepository;
    private final WorkLogSettingsMapper workLogSettingsMapper;

    @Override
    @Transactional(readOnly = true)
    public WorkLogSettings getSettings() {

        return workLogSettingsRepository.findFirstByActiveTrue()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Work log settings not found.", ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkLogSettingsResponse getWorkLogSettings() {

        return workLogSettingsMapper.toResponse(getSettings());
    }

    @Override
    public WorkLogSettingsResponse updateWorkLogSettings(
            WorkLogSettingsRequest request) {

        validateWorkLogSettings(request);

        WorkLogSettings settings = getSettings();

        workLogSettingsMapper.updateEntity(request, settings);

        WorkLogSettings updated =
                workLogSettingsRepository.save(settings);

        return workLogSettingsMapper.toResponse(updated);
    }

    private void validateWorkLogSettings(
            WorkLogSettingsRequest request) {

        validateReminderSettings(request);
        validateSubmissionSettings(request);
        validateDescriptionSettings(request);
        validateApprovalSettings(request);
    }

    private void validateReminderSettings(
            WorkLogSettingsRequest request) {

        if (request.getWorkLogReminderEnabled()
                && request.getReminderIntervalMinutes() <= 0) {

            throw new BadRequestException(
                    "Reminder interval must be greater than zero.", ErrorCode.VALIDATION_FAILED);
        }

        if (!request.getWorkLogReminderEnabled()
                && request.getReminderIntervalMinutes() > 0) {

            throw new BadRequestException(
                    "Reminder interval must be zero when reminders are disabled.", ErrorCode.VALIDATION_FAILED);
        }
    }

    private void validateSubmissionSettings(
            WorkLogSettingsRequest request) {

        if (!request.getWorkLogSubmissionRequired()
                && request.getReportRequiredBeforeCheckout()) {

            throw new BadRequestException(
                    "Report before checkout cannot be enabled when work log submission is disabled.", ErrorCode.VALIDATION_FAILED);
        }

        if (request.getManagerApprovalRequired()
                && !request.getWorkLogSubmissionRequired()) {

            throw new BadRequestException(
                    "Manager approval requires work log submission to be enabled.", ErrorCode.VALIDATION_FAILED);
        }
    }

    private void validateDescriptionSettings(
            WorkLogSettingsRequest request) {

        if (request.getMinimumWorkLogEntries() <= 0) {

            throw new BadRequestException(
                    "Minimum work log entries must be greater than zero.", ErrorCode.VALIDATION_FAILED);
        }

        if (request.getMinimumWorkLogDescriptionLength() < 10) {

            throw new BadRequestException(
                    "Minimum work log description length must be at least 10 characters.", ErrorCode.VALIDATION_FAILED);
        }
    }

    private void validateApprovalSettings(
            WorkLogSettingsRequest request) {

        if (request.getManagerApprovalRequired()
                && request.getAllowMultipleReportSubmissionsPerDay()) {

            throw new BadRequestException(
                    "Multiple report submissions cannot be enabled when manager approval is required.", ErrorCode.VALIDATION_FAILED);
        }
    }
}
