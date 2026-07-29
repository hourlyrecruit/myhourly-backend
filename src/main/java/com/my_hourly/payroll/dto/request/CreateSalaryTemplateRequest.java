package com.my_hourly.payroll.dto.request;

import com.my_hourly.employee.entity.EmploymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create salary template")
public class CreateSalaryTemplateRequest {

    @NotNull(message = "Employee Type is required.")
    @Schema(description = "Employee Type", example = "FULL_TIME")
    private EmploymentType employeeType;

    /* ==========================
       Earnings
       ========================== */

    @NotNull(message = "Basic Salary is required.")
    @DecimalMin(value = "0.00", message = "Basic Salary cannot be negative.")
    @Schema(example = "30000")
    private BigDecimal basicSalary;

    @NotNull(message = "HRA is required.")
    @DecimalMin(value = "0.00", message = "HRA cannot be negative.")
    @Schema(example = "12000")
    private BigDecimal hra;

    @NotNull(message = "Special Allowance is required.")
    @DecimalMin(value = "0.00", message = "Special Allowance cannot be negative.")
    @Schema(example = "5000")
    private BigDecimal specialAllowance;

    @NotNull(message = "Medical Allowance is required.")
    @DecimalMin(value = "0.00", message = "Medical Allowance cannot be negative.")
    @Schema(example = "1500")
    private BigDecimal medicalAllowance;

    @NotNull(message = "Travel Allowance is required.")
    @DecimalMin(value = "0.00", message = "Travel Allowance cannot be negative.")
    @Schema(example = "2000")
    private BigDecimal travelAllowance;

    @NotNull(message = "Bonus is required.")
    @DecimalMin(value = "0.00", message = "Bonus cannot be negative.")
    @Schema(example = "3000")
    private BigDecimal bonus;

    @NotNull(message = "Other Allowance is required.")
    @DecimalMin(value = "0.00", message = "Other Allowance cannot be negative.")
    @Schema(example = "1000")
    private BigDecimal otherAllowance;

    /* ==========================
       Deductions
       ========================== */

    @NotNull(message = "PF is required.")
    @DecimalMin(value = "0.00", message = "PF cannot be negative.")
    @Schema(example = "1800")
    private BigDecimal pf;

    @NotNull(message = "ESI is required.")
    @DecimalMin(value = "0.00", message = "ESI cannot be negative.")
    @Schema(example = "0")
    private BigDecimal esi;

    @NotNull(message = "Professional Tax is required.")
    @DecimalMin(value = "0.00", message = "Professional Tax cannot be negative.")
    @Schema(example = "200")
    private BigDecimal professionalTax;

    @NotNull(message = "Income Tax is required.")
    @DecimalMin(value = "0.00", message = "Income Tax cannot be negative.")
    @Schema(example = "0")
    private BigDecimal incomeTax;

    @NotNull(message = "Other Deduction is required.")
    @DecimalMin(value = "0.00", message = "Other Deduction cannot be negative.")
    @Schema(example = "0")
    private BigDecimal otherDeduction;

}
