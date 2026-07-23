package com.my_hourly.leave.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "leave_balances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "employee_id",
                        "leave_type_id",
                        "year"
                })
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private Integer year;

    /**
     * Total annual leave allocation (e.g. 24 days).
     */
    @Column(nullable = false)
    private Integer allocatedLeaves;

    /**
     * Total approved leave days consumed so far.
     */
    @Column(nullable = false)
    private Integer usedLeaves;

    /**
     * Cumulative days expired via month-end scheduler (carry-forward OFF only).
     */
    @Column(nullable = false)
    private Integer expiredLeaves;

    /**
     * Available balance: allocatedLeaves - usedLeaves - expiredLeaves.
     */
    @Column(nullable = false)
    private Integer remainingLeaves;

}