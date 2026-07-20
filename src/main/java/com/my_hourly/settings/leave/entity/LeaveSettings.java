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

    /**
     * When true, unused monthly guideline days carry forward into the annual balance.
     * When false, unused days expire at month-end.
     */
    @Column(nullable = false)
    private Boolean carryForwardAllowed;

    /**
     * Recommended leave days per month used to calculate expiry when carryForwardAllowed = false.
     * Default: 2 (i.e. 24 annual / 12 months).
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer monthlyGuideline = 2;

    /**
     * Total annual paid leave days allocated to employees (e.g. 24).
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer annualPaidLeave = 24;

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
