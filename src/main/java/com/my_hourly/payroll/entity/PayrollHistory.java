package com.my_hourly.payroll.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.payroll.enums.PayrollHistoryAction;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payroll_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollHistory extends BaseEntity {

    /**
     * Payroll
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payroll_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payroll_history_payroll")
    )
    private Payroll payroll;

    /**
     * Action
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PayrollHistoryAction action;

    /**
     * Action Performed By
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "performed_by",
            foreignKey = @ForeignKey(name = "fk_payroll_history_employee")
    )
    private Employee performedBy;

    /**
     * Remarks
     */
    @Column(length = 500)
    private String remarks;
}
