package com.my_hourly.payroll.entity;

import com.my_hourly.common.entity.BaseEntity;

import com.my_hourly.employee.entity.EmploymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "salary_templates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_salary_template_employee_type",
                        columnNames = "employee_type_id"
                )
        }
)
public class SalaryTemplate extends BaseEntity {

    /**
     * One active template per Employee Type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false, length = 30)
    private EmploymentType employeeType;

    /* ===========================
       Earnings
       =========================== */

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal hra;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal specialAllowance;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal travelAllowance;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal bonus;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal otherAllowance;

    /* ===========================
       Gross
       =========================== */

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary;

    /* ===========================
       Deductions
       =========================== */

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pf;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal esi;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal professionalTax;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal incomeTax;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal otherDeduction;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

}
