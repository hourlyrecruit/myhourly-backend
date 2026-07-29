package com.my_hourly.payroll.dto.response;

import com.my_hourly.payroll.enums.PayrollHistoryAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollHistoryResponse {

    private Long id;

    private PayrollHistoryAction action;

    private String performedBy;

    private String remarks;

    private LocalDateTime createdAt;

}
