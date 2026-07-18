package com.my_hourly.settings.leave.mapper;

import com.my_hourly.settings.leave.dto.request.LeaveSettingsRequest;
import com.my_hourly.settings.leave.dto.response.LeaveSettingsResponse;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import org.springframework.stereotype.Component;

@Component
public class LeaveSettingsMapper {

    public LeaveSettingsResponse toResponse(LeaveSettings entity) {

        if (entity == null) {
            return null;
        }

        return LeaveSettingsResponse.builder()
                .id(entity.getId())
                .halfDayLeaveAllowed(entity.getHalfDayLeaveAllowed())
                .carryForwardAllowed(entity.getCarryForwardAllowed())
                .maximumCarryForwardDays(entity.getMaximumCarryForwardDays())
                .minimumAdvanceNoticeDays(entity.getMinimumAdvanceNoticeDays())
                .maximumAdvanceNoticeDays(entity.getMaximumAdvanceNoticeDays())
                .maximumConsecutiveLeaveDays(entity.getMaximumConsecutiveLeaveDays())
                .managerApprovalRequired(entity.getManagerApprovalRequired())
                .hrApprovalRequired(entity.getHrApprovalRequired())
                .allowLeaveOnHoliday(entity.getAllowLeaveOnHoliday())
                .allowLeaveOnWeekend(entity.getAllowLeaveOnWeekend())
                .autoApproveLeave(entity.getAutoApproveLeave())
                .allowNegativeLeaveBalance(entity.getAllowNegativeLeaveBalance())
                .allowBackdatedLeaveApplication(entity.getAllowBackdatedLeaveApplication())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(
            LeaveSettingsRequest request,
            LeaveSettings entity) {

        entity.setHalfDayLeaveAllowed(request.getHalfDayLeaveAllowed());
        entity.setCarryForwardAllowed(request.getCarryForwardAllowed());
        entity.setMaximumCarryForwardDays(request.getMaximumCarryForwardDays());
        entity.setMinimumAdvanceNoticeDays(request.getMinimumAdvanceNoticeDays());
        entity.setMaximumAdvanceNoticeDays(request.getMaximumAdvanceNoticeDays());
        entity.setMaximumConsecutiveLeaveDays(request.getMaximumConsecutiveLeaveDays());
        entity.setManagerApprovalRequired(request.getManagerApprovalRequired());
        entity.setHrApprovalRequired(request.getHrApprovalRequired());
        entity.setAllowLeaveOnHoliday(request.getAllowLeaveOnHoliday());
        entity.setAllowLeaveOnWeekend(request.getAllowLeaveOnWeekend());
        entity.setAutoApproveLeave(request.getAutoApproveLeave());
        entity.setAllowNegativeLeaveBalance(request.getAllowNegativeLeaveBalance());
        entity.setAllowBackdatedLeaveApplication(request.getAllowBackdatedLeaveApplication());
    }
}