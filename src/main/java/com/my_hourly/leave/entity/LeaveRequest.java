package com.my_hourly.leave.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer totalDays;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LeaveStatus status;

    /**
     * Days of this request that were approved as PAID, i.e. deducted from the
     * annual leave balance.
     *
     * <p>Null until the request is approved. The monthly paid-leave guideline
     * from LeaveSettings decides how many of the request's days can be PAID;
     * the rest — and any days the annual balance cannot cover — become
     * {@link #lopDays}.</p>
     */
    @Column(name = "paid_days")
    private Integer paidDays;

    /**
     * Days of this request that were approved as LOP (Loss of Pay). LOP days are
     * never deducted from the annual leave balance.
     *
     * <p>Null until the request is approved. A request may be entirely PAID,
     * entirely LOP, or a mix of both.</p>
     */
    @Column(name = "lop_days")
    private Integer lopDays;

    /**
     * The authenticated user who approved this request; null while pending.
     *
     * <p>{@link LeaveApproval} remains the per-action audit trail — this is the
     * denormalised pointer the leave list/detail API reads.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

//    @Column(length = 500)
//    private String rejectionReason;

}