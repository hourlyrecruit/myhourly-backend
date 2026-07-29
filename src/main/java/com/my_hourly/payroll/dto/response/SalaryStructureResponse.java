package com.my_hourly.payroll.dto.response;

import com.my_hourly.payroll.enums.SalaryStructureStatus;
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
@Schema(description = "Salary Structure Response")
public class SalaryStructureResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "15")
    private Long employeeId;

    @Schema(example = "EMP00015")
    private String employeeCode;

    @Schema(example = "Rahul Sharma")
    private String employeeName;

    @Schema(example = "2")
    private Long salaryTemplateId;

    /* ==========================
       Effective Dates
       ========================== */

    @Schema(example = "2026-08-01")
    private LocalDate effectiveFrom;

    @Schema(example = "2027-03-31")
    private LocalDate effectiveTo;

    /* ==========================
       Earnings
       ========================== */

    private BigDecimal basicSalary;

    private BigDecimal hra;

    private BigDecimal specialAllowance;

    private BigDecimal medicalAllowance;

    private BigDecimal travelAllowance;

    private BigDecimal bonus;

    private BigDecimal otherAllowance;

    private BigDecimal grossSalary;

    /* ==========================
       Deductions
       ========================== */

    private BigDecimal pf;

    private BigDecimal esi;

    private BigDecimal professionalTax;

    private BigDecimal incomeTax;

    private BigDecimal otherDeduction;

    /* ==========================
       Final Salary
       ========================== */

    private BigDecimal netSalary;

    @Schema(example = "ACTIVE")
    private SalaryStructureStatus status;

    @Schema(example = "Annual Salary Revision")
    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
