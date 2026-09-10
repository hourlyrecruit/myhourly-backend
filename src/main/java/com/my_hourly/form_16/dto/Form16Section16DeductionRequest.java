package com.my_hourly.form_16.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Section16DeductionRequest {

    private BigDecimal salaryReceivedFromOtherEmployers;

    private BigDecimal standardDeductionSection16I;

    private BigDecimal entertainmentAllowanceSection16II;

    private BigDecimal taxOnEmploymentSection16III;

    private BigDecimal incomeLossHouseProperty;

    private BigDecimal incomeUnderOtherSources;
}