package com.my_hourly.payroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create employee salary structure")
public class CreateSalaryStructureRequest {

    @NotNull(message = "Employee Id is required.")
    @Schema(example = "1")
    private Long employeeId;

    @NotNull(message = "Salary Template Id is required.")
    @Schema(example = "2")
    private Long salaryTemplateId;

    @NotNull(message = "Effective From is required.")
    @Schema(example = "2026-08-01")
    private LocalDate effectiveFrom;

    @Schema(example = "2027-08-01")
    @Builder.Default
    private LocalDate effectiveTo = null;

    @Schema(example = "Annual Salary Revision")
    private String remarks;
}
