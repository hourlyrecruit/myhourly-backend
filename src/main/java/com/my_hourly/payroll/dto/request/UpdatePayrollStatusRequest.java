package com.my_hourly.payroll.dto.request;

import com.my_hourly.payroll.enums.PayrollStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePayrollStatusRequest {

    @NotNull(message = "Status cannot be null")
    private PayrollStatus status;

    private String paymentReference;

}
