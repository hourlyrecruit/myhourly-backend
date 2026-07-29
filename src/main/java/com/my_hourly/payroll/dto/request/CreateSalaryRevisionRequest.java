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
public class CreateSalaryRevisionRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long salaryTemplateId;

    @NotNull
    private LocalDate effectiveFrom;

    @Schema(example = "Annual Appraisal 2026")
    private String remarks;
}
