package com.my_hourly.form_16.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16SalaryRequest {

    private BigDecimal salaryUnderSection17_1;

    private BigDecimal perquisitesUnderSection17_2;

    private BigDecimal profitsInLieuOfSalaryUnderSection17_3;

    private BigDecimal salaryReceivedFromOtherEmployers;
}
