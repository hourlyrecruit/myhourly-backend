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

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean halfDayLeaveAllowed;

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

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Integer minimumAdvanceNoticeDays;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Integer maximumAdvanceNoticeDays;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Integer maximumConsecutiveLeaveDays;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean managerApprovalRequired;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean hrApprovalRequired;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean allowLeaveOnHoliday;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean allowLeaveOnWeekend;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean autoApproveLeave;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean allowNegativeLeaveBalance;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean allowBackdatedLeaveApplication;

    // NOTE: removed — duplicate `active` field shadowed the one inherited from
    // BaseSettings (this.active vs super.active ambiguity). The inherited
    // BaseSettings#active is the column actually used.
//    @Column(nullable = false)
//    @Builder.Default
//    private Boolean active = true;

    /**
     * When enabled, taking leave on Monday counts the preceding Saturday and Sunday,
     * totaling 3 chargeable days.
     */
    @Column(name = "sandwich_leave_monday_enabled", nullable = false)
    @Builder.Default
    private Boolean sandwichLeaveMondayEnabled = false;

    /**
     * When enabled, taking leave on Friday counts the following Saturday and Sunday,
     * totaling 3 chargeable days.
     */
    @Column(name = "sandwich_leave_friday_enabled", nullable = false)
    @Builder.Default
    private Boolean sandwichLeaveFridayEnabled = false;

    /**
     * When enabled, taking leave on both Friday and Monday counts the intervening
     * Saturday and Sunday, totaling 4 chargeable days. This rule takes precedence
     * over the individual Friday and Monday rules.
     */
    @Column(name = "sandwich_leave_friday_monday_enabled", nullable = false)
    @Builder.Default
    private Boolean sandwichLeaveFridayMondayEnabled = false;
}
