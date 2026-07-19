package com.my_hourly.leave.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.enums.MonthType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "leave_balances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "employee_id",
                        "leave_type_id",
                        "year",
                        "month"
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MonthType month;

    @Column(nullable = false)
    private Integer allocatedLeaves;

    @Column(nullable = false)
    private Integer carriedForwardLeaves;

    @Column(nullable = false)
    private Integer usedLeaves;

    @Column(nullable = false)
    private Integer expiredLeaves;

    @Column(nullable = false)
    private Integer remainingLeaves;

}