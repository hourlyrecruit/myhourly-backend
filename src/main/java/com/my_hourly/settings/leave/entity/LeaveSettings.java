package com.my_hourly.settings.leave.entity;

import com.my_hourly.settings.BaseSettings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "leave_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveSettings extends BaseSettings {

    @Column(nullable = false)
    private Boolean halfDayLeaveAllowed;

    @Column(nullable = false)
    private Boolean carryForwardAllowed;

    @Column(nullable = false)
    private Integer maximumCarryForwardDays;

    @Column(nullable = false)
    private Integer minimumAdvanceNoticeDays;

    @Column(nullable = false)
    private Integer maximumAdvanceNoticeDays;

    @Column(nullable = false)
    private Integer maximumConsecutiveLeaveDays;

    @Column(nullable = false)
    private Boolean managerApprovalRequired;

    @Column(nullable = false)
    private Boolean hrApprovalRequired;

    @Column(nullable = false)
    private Boolean allowLeaveOnHoliday;

    @Column(nullable = false)
    private Boolean allowLeaveOnWeekend;

    @Column(nullable = false)
    private Boolean autoApproveLeave;

    @Column(nullable = false)
    private Boolean allowNegativeLeaveBalance;

    @Column(nullable = false)
    private Boolean allowBackdatedLeaveApplication;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
