package com.my_hourly.form_16.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16SalaryRequest {

    /**
     * 1(a)
     * Salary as per provisions contained in section 17(1)
     */
    private BigDecimal salaryUnderSection17_1;


    /**
     * 1(b)
     * Value of perquisites under section 17(2)
     */
    private BigDecimal perquisitesUnderSection17_2;


    /**
     * 1(c)
     * Profits in lieu of salary under section 17(3)
     */
    private BigDecimal profitsInLieuOfSalaryUnderSection17_3;


    /**
     * 1(d)
     * Total / Gross Salary
     */
    private BigDecimal grossSalary;


    /**
     * 1(e)
     * Reported total amount of salary received
     * from other employer(s)
     */
    private BigDecimal salaryReceivedFromOtherEmployers;
}