package com.my_hourly.payroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update a DRAFT payroll before finalization")
public class UpdateDraftPayrollRequest {

    /* ==========================
       Attendance
       ========================== */

    @Schema(description = "Total working days in the month", example = "30")
    @Min(value = 1, message = "Total working days must be at least 1.")
    private Integer totalWorkingDays;

    @Schema(description = "Number of days the employee worked", example = "28")
    @Min(value = 0, message = "Worked days cannot be negative.")
    private Integer workedDays;

    @Schema(description = "LOP (Loss of Pay) days", example = "2")
    @Min(value = 0, message = "LOP days cannot be negative.")
    private Integer lopDays;

    /* ==========================
       Earnings
       ========================== */

    @DecimalMin(value = "0.00", message = "Basic Salary cannot be negative.")
    @Schema(example = "30000")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.00", message = "HRA cannot be negative.")
    @Schema(example = "12000")
    private BigDecimal hra;

    @DecimalMin(value = "0.00", message = "Special Allowance cannot be negative.")
    @Schema(example = "5000")
    private BigDecimal specialAllowance;

    @DecimalMin(value = "0.00", message = "Medical Allowance cannot be negative.")
    @Schema(example = "1500")
    private BigDecimal medicalAllowance;

    @DecimalMin(value = "0.00", message = "Travel Allowance cannot be negative.")
    @Schema(example = "2000")
    private BigDecimal travelAllowance;

    @DecimalMin(value = "0.00", message = "Bonus cannot be negative.")
    @Schema(example = "3000")
    private BigDecimal bonus;

    @DecimalMin(value = "0.00", message = "Other Allowance cannot be negative.")
    @Schema(example = "1000")
    private BigDecimal otherAllowance;

    /* ==========================
       Deductions
       ========================== */

    @DecimalMin(value = "0.00", message = "PF cannot be negative.")
    @Schema(example = "1800")
    private BigDecimal pf;

    @DecimalMin(value = "0.00", message = "ESI cannot be negative.")
    @Schema(example = "0")
    private BigDecimal esi;

    @DecimalMin(value = "0.00", message = "Professional Tax cannot be negative.")
    @Schema(example = "200")
    private BigDecimal professionalTax;

    @DecimalMin(value = "0.00", message = "Income Tax cannot be negative.")
    @Schema(example = "0")
    private BigDecimal incomeTax;

    @DecimalMin(value = "0.00", message = "Other Deduction cannot be negative.")
    @Schema(example = "0")
    private BigDecimal otherDeduction;

    @Schema(description = "Payroll remarks")
    private String remarks;
}
