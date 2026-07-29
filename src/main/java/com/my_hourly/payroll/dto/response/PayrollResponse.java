package com.my_hourly.payroll.dto.response;

import com.my_hourly.payroll.enums.PayrollStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payroll Response")
public class PayrollResponse {

    private Long id;

    private String payrollNumber;

    private Integer version;

    private Boolean active;

    /* ==========================================
       Employee Snapshot
       ========================================== */

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private String departmentName;

    private String designationName;

    /* ==========================================
       Payment Details Snapshot
       ========================================== */

    private String panNumber;

    private String bankName;

    private String accountNumber;

    private String ifscCode;

    /* ==========================================
       Payroll Details
       ========================================== */

    private LocalDate payrollMonth;

    private PayrollStatus status;

    /* ==========================================
       Attendance Snapshot
       ========================================== */

    private Integer totalWorkingDays;

    private Integer workedDays;

    private Integer lopDays;

    private Integer payableDays;

    /* ==========================================
       Earnings
       ========================================== */

    private BigDecimal basicSalary;

    private BigDecimal hra;

    private BigDecimal specialAllowance;

    private BigDecimal medicalAllowance;

    private BigDecimal travelAllowance;

    private BigDecimal bonus;

    private BigDecimal otherAllowance;

    private BigDecimal grossSalary;

    /* ==========================================
       Deductions
       ========================================== */

    private BigDecimal lopAmount;

    private BigDecimal pf;

    private BigDecimal esi;

    private BigDecimal professionalTax;

    private BigDecimal incomeTax;

    private BigDecimal otherDeduction;

    private BigDecimal totalDeduction;

    /* ==========================================
       Final Salary
       ========================================== */

    private BigDecimal netPayable;

    /* ==========================================
       Approval
       ========================================== */

    private String approvedBy;

    private LocalDate approvedDate;

    /* ==========================================
       Payment
       ========================================== */

    private LocalDate paymentDate;

    private String paymentReference;

    /* ==========================================
       Remarks
       ========================================== */

    private String remarks;

    /* ==========================================
       Audit
       ========================================== */

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
