package com.my_hourly.payroll.dto.response;

import com.my_hourly.employee.entity.EmploymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Salary Template Response")
public class SalaryTemplateResponse {

    @Schema(description = "Salary Template Id", example = "1")
    private Long id;

    @Schema(description = "Employee Type", example = "FULL_TIME")
    private EmploymentType employeeType;

    /* ==========================
       Earnings
       ========================== */

    @Schema(description = "Basic Salary", example = "30000.00")
    private BigDecimal basicSalary;

    @Schema(description = "House Rent Allowance", example = "12000.00")
    private BigDecimal hra;

    @Schema(description = "Special Allowance", example = "5000.00")
    private BigDecimal specialAllowance;

    @Schema(description = "Medical Allowance", example = "1500.00")
    private BigDecimal medicalAllowance;

    @Schema(description = "Travel Allowance", example = "2000.00")
    private BigDecimal travelAllowance;

    @Schema(description = "Bonus", example = "3000.00")
    private BigDecimal bonus;

    @Schema(description = "Other Allowance", example = "1000.00")
    private BigDecimal otherAllowance;

    /* ==========================
       Gross Salary
       ========================== */

    @Schema(description = "Gross Salary", example = "54500.00")
    private BigDecimal grossSalary;

    /* ==========================
       Deductions
       ========================== */

    @Schema(description = "Provident Fund", example = "1800.00")
    private BigDecimal pf;

    @Schema(description = "Employee State Insurance", example = "0.00")
    private BigDecimal esi;

    @Schema(description = "Professional Tax", example = "200.00")
    private BigDecimal professionalTax;

    @Schema(description = "Income Tax", example = "0.00")
    private BigDecimal incomeTax;

    @Schema(description = "Other Deduction", example = "0.00")
    private BigDecimal otherDeduction;

    @Schema(description = "Active Status")
    private Boolean active;

    @Schema(description = "Created At")
    private LocalDateTime createdAt;

    @Schema(description = "Updated At")
    private LocalDateTime updatedAt;

}
