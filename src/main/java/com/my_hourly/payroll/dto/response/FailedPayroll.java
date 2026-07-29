package com.my_hourly.payroll.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedPayroll {

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private String reason;

}