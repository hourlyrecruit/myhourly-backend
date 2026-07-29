package com.my_hourly.payroll.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payroll Generation Summary")
public class PayrollSummaryResponse {

    /**
     * Payroll Month
     */
    private LocalDate payrollMonth;

    /**
     * Total Employees Processed
     */
    private Integer totalEmployees;

    /**
     * Successfully Generated
     */
    private Integer generated;

    /**
     * Failed Payroll Count
     */
    private Integer failed;

    /**
     * Generated Payroll Numbers
     */
    @Builder.Default
    private List<String> generatedPayrolls = new ArrayList<>();

    /**
     * Failed Employee Details
     */
    @Builder.Default
    private List<FailedPayroll> failedEmployees = new ArrayList<>();

}
