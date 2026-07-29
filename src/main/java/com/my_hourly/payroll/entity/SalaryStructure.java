package com.my_hourly.payroll.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.payroll.enums.SalaryStructureStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "salary_structures",
        indexes = {
                @Index(name = "idx_salary_structure_employee", columnList = "employee_id"),
                @Index(name = "idx_salary_structure_status", columnList = "status"),
                @Index(name = "idx_salary_structure_effective_from", columnList = "effective_from")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructure extends BaseEntity {

    /**
     * Employee
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_salary_structure_employee")
    )
    private Employee employee;

    /**
     * Source Salary Template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "salary_template_id",
            foreignKey = @ForeignKey(name = "fk_salary_structure_template")
    )
    private SalaryTemplate salaryTemplate;

    /**
     * Effective Dates
     */
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    /* =====================================================
       Earnings
       ===================================================== */

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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary;

    /* =====================================================
       Deductions
       ===================================================== */

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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryStructureStatus status;

    /**
     * Remarks
     */
    @Column(length = 500)
    private String remarks;
}