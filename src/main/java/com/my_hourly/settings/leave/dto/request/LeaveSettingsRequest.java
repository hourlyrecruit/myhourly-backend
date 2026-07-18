package com.my_hourly.settings.leave.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveSettingsRequest {

    @NotNull
    private Boolean halfDayLeaveAllowed;

    @NotNull
    private Boolean carryForwardAllowed;

    @NotNull
    @Min(0)
    private Integer maximumCarryForwardDays;

    @NotNull
    @Min(0)
    private Integer minimumAdvanceNoticeDays;

    @NotNull
    @Min(0)
    private Integer maximumAdvanceNoticeDays;

    @NotNull
    @Min(1)
    private Integer maximumConsecutiveLeaveDays;

    @NotNull
    private Boolean managerApprovalRequired;

    @NotNull
    private Boolean hrApprovalRequired;

    @NotNull
    private Boolean allowLeaveOnHoliday;

    @NotNull
    private Boolean allowLeaveOnWeekend;

    @NotNull
    private Boolean autoApproveLeave;

    @NotNull
    private Boolean allowNegativeLeaveBalance;

    @NotNull
    private Boolean allowBackdatedLeaveApplication;

}
