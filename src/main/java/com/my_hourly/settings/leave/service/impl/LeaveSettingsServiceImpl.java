package com.my_hourly.settings.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.settings.leave.dto.request.LeaveSettingsRequest;
import com.my_hourly.settings.leave.dto.response.LeaveSettingsResponse;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.mapper.LeaveSettingsMapper;
import com.my_hourly.settings.leave.repository.LeaveSettingsRepository;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveSettingsServiceImpl implements LeaveSettingsService {

    private final LeaveSettingsRepository leaveSettingsRepository;
    private final LeaveSettingsMapper leaveSettingsMapper;

    @Override
    @Transactional(readOnly = true)
    public LeaveSettings getSettings() {

        return leaveSettingsRepository.findFirstByActiveTrue()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Leave settings not found.",ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveSettingsResponse getLeaveSettings() {

        LeaveSettings leaveSettings = getSettings();

        return leaveSettingsMapper.toResponse(leaveSettings);
    }

    @Override
    public LeaveSettingsResponse updateLeaveSettings(LeaveSettingsRequest request) {

        validateLeaveSettings(request);

        LeaveSettings leaveSettings = getSettings();

        leaveSettingsMapper.updateEntity(request, leaveSettings);

        LeaveSettings updatedSettings = leaveSettingsRepository.save(leaveSettings);

        return leaveSettingsMapper.toResponse(updatedSettings);
    }

    private void validateLeaveSettings(LeaveSettingsRequest request) {

        // Minimum advance notice cannot exceed maximum advance notice
        if (request.getMinimumAdvanceNoticeDays()
                > request.getMaximumAdvanceNoticeDays()) {

            throw new BadRequestException(
                    "Minimum advance notice cannot be greater than maximum advance notice.", ErrorCode.VALIDATION_FAILED);
        }

        // Carry forward validations removed because maxCarryForwardDays is replaced by monthlyGuideline.

        // Auto approval validation
        if (request.getAutoApproveLeave()
                && (request.getManagerApprovalRequired()
                || request.getHrApprovalRequired())) {

            throw new BadRequestException(
                    "Manager and HR approval cannot be required when auto approval is enabled.", ErrorCode.VALIDATION_FAILED);
        }

        // Maximum consecutive leave validation
        if (request.getMaximumConsecutiveLeaveDays()
                > request.getMaximumAdvanceNoticeDays()) {

            throw new BadRequestException(
                    "Maximum consecutive leave days cannot exceed maximum advance notice days.", ErrorCode.VALIDATION_FAILED);
        }
    }
}