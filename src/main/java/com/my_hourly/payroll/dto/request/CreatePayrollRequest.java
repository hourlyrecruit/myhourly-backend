package com.my_hourly.payroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to generate payroll")
public class CreatePayrollRequest {

    @NotNull(message = "Payroll month is required.")
    @Schema(
            description = "Payroll month (Use first day of month)",
            example = "2026-08-01"
    )
    private LocalDate payrollMonth;

    @Schema(
            description = "Employee Ids. Leave empty to generate payroll for all active employees."
    )
    private List<Long> employeeIds;

    @Schema(
            description = "Payroll remarks",
            example = "August 2026 Payroll"
    )
    private String remarks;

    @Schema(
            description = "If true, payroll is saved as DRAFT instead of GENERATED. Defaults to false.",
            example = "false"
    )
    @Builder.Default
    private Boolean saveAsDraft = false;
}
