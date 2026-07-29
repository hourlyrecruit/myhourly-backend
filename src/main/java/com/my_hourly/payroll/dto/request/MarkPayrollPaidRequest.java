package com.my_hourly.payroll.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkPayrollPaidRequest {

    @NotBlank
    private String paymentReference;

    private LocalDate paymentDate;

}