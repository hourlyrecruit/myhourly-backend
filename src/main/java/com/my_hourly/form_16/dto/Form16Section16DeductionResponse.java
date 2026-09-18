package com.my_hourly.form_16.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Section16DeductionResponse {

    private Long id;

    private Long form16Id;

    // 3
    private BigDecimal salaryReceivedFromCurrentEmployer;

    // 1(e)
    private BigDecimal salaryReceivedFromOtherEmployers;

    // 4(a)
    private BigDecimal standardDeductionSection16I;

    // 4(b)
    private BigDecimal entertainmentAllowanceSection16II;

    // 4(c)
    private BigDecimal taxOnEmploymentSection16III;

    // 5 - AUTOMATIC
    private BigDecimal totalDeductionsSection16;

    // 6 - AUTOMATIC
    private BigDecimal incomeChargeableUnderSalaries;

    // 7(a)
    private BigDecimal incomeLossHouseProperty;

    // 7(b)
    private BigDecimal incomeUnderOtherSources;

    // 8 - AUTOMATIC
    private BigDecimal totalOtherIncome;

    // 9 - AUTOMATIC
    private BigDecimal grossTotalIncome;
}