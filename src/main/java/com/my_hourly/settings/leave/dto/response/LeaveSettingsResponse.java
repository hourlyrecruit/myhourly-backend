package com.my_hourly.settings.leave.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveSettingsResponse {

    private Long id;

    private Boolean halfDayLeaveAllowed;

    private Boolean carryForwardAllowed;

    private Integer maximumCarryForwardDays;

    private Integer minimumAdvanceNoticeDays;

    private Integer maximumAdvanceNoticeDays;

    private Integer maximumConsecutiveLeaveDays;

    private Boolean managerApprovalRequired;

    private Boolean hrApprovalRequired;

    private Boolean allowLeaveOnHoliday;

    private Boolean allowLeaveOnWeekend;

    private Boolean autoApproveLeave;

    private Boolean allowNegativeLeaveBalance;

    private Boolean allowBackdatedLeaveApplication;

    private Boolean active;

}
