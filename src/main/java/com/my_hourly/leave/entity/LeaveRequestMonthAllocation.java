package com.my_hourly.leave.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * How one approved leave request's days were attributed to a single calendar
 * month.
 *
 * <p>The monthly paid-leave allowance is counted per employee and calendar
 * month, by the dates the leave days actually fall on. A request that spans a
 * month boundary therefore produces one row per touched month, and the
 * monthly-usage queries sum {@code paidDays} over these rows instead of
 * attributing the whole request to its start month.</p>
 *
 * <p>Written once at approval time for classified (paid) leave types; a
 * request can only be approved once, so rows are insert-only. Historical
 * requests approved before this table existed have no rows and are handled by
 * the legacy start-date fallback in {@code LeaveRequestRepository}.</p>
 */
@Entity
@Table(
        name = "leave_request_month_allocations",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_leave_request_month_allocations",
                columnNames = {"leave_request_id", "allocation_month"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestMonthAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_request_id", nullable = false)
    private LeaveRequest leaveRequest;

    /**
     * First day of the calendar month this row describes.
     */
    @Column(name = "allocation_month", nullable = false)
    private LocalDate allocationMonth;

    /**
     * Days of the request in this month that were classified PAID (deducted
     * from the annual balance).
     */
    @Column(name = "paid_days", nullable = false)
    private Integer paidDays;

    /**
     * Working days of the request that fell in this month (weekends and
     * holidays excluded) - i.e. {@code paidDays + lopDays} for the month.
     */
    @Column(name = "working_days", nullable = false)
    private Integer workingDays;
}
