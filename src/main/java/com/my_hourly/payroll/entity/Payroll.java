package com.my_hourly.payroll.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.payroll.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "payrolls",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payroll_number",
                        columnNames = "payroll_number"
                )
        },
        indexes = {
                @Index(name = "idx_payroll_employee", columnList = "employee_id"),
                @Index(name = "idx_payroll_month", columnList = "payroll_month"),
                @Index(name = "idx_payroll_status", columnList = "status"),
                @Index(name = "idx_payroll_active", columnList = "active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll extends BaseEntity {

    /**
     * Example: PR-202608-0001
     */
    @Column(name = "payroll_number", nullable = false, unique = true, length = 30)
    private String payrollNumber;

    /**
     * Payroll Version: increments on each regenerate
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    /**
     * Active flag: false for SUPERSEDED / CANCELLED versions
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /* =====================================================
       Employee Reference
       ===================================================== */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payroll_employee")
    )
    private Employee employee;

    /* =====================================================
       Salary Structure Reference
       ===================================================== */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "salary_structure_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payroll_salary_structure")
    )
    private SalaryStructure salaryStructure;

    /**
     * Payroll Month (always 1st day of the month)
     */
    @Column(name = "payroll_month", nullable = false)
    private LocalDate payrollMonth;

    /* =====================================================
       Employee Snapshot (captured at generation time)
       ===================================================== */

    @Column(name = "employee_name", nullable = false, length = 100)
    private String employeeName;

    @Column(name = "employee_code", nullable = false, length = 30)
    private String employeeCode;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "designation_name", length = 100)
    private String designationName;

    /* =====================================================
       Payment Details Snapshot (captured at generation time)
       ===================================================== */

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 30)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "uan", length = 20)
    private String uanNumber;



    /* =====================================================
       Attendance Snapshot
       ===================================================== */

    @Column(name = "total_working_days", nullable = false)
    private Integer totalWorkingDays;

    @Column(name = "worked_days", nullable = false)
    private Integer workedDays;

    @Column(name = "lop_days", nullable = false)
    @Builder.Default
    private Integer lopDays = 0;

    @Column(name = "payable_days", nullable = false)
    private Integer payableDays;

    /* =====================================================
       Earnings Snapshot
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
       Deductions Snapshot
       ===================================================== */

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal lopAmount = BigDecimal.ZERO;

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
    private BigDecimal totalDeduction;

    /* =====================================================
       Final Salary
       ===================================================== */

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netPayable;

    /* =====================================================
       Approval
       ===================================================== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "approved_by",
            foreignKey = @ForeignKey(name = "fk_payroll_approved_by")
    )
    private Employee approvedBy;

    private LocalDate approvedDate;

    /* =====================================================
       Payment
       ===================================================== */

    private LocalDate paymentDate;

    @Column(length = 100)
    private String paymentReference;

    /* =====================================================
       Remarks
       ===================================================== */

    @Column(length = 500)
    private String remarks;
}
