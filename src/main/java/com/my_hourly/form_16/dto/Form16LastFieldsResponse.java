package com.my_hourly.form_16.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16LastFieldsResponse {

    private Long id;

    private Long form16Id;


    // =========================================================
    // 13
    // =========================================================

    private BigDecimal taxOnTotalIncome;


    // =========================================================
    // 14
    // =========================================================

    private BigDecimal rebateUnderSection87A;


    // =========================================================
    // 15
    // =========================================================

    private BigDecimal surcharge;


    // =========================================================
    // 16
    // =========================================================

    private BigDecimal healthAndEducationCess;


    // =========================================================
    // 17 - CALCULATED
    // =========================================================

    private BigDecimal taxPayable;


    // =========================================================
    // 18
    // =========================================================

    private BigDecimal reliefUnderSection89;


    // =========================================================
    // 19
    // =========================================================

    private BigDecimal taxDeductedAtSourceForm12BAA;


    // =========================================================
    // 20
    // =========================================================

    private BigDecimal taxCollectedAtSourceForm12BAA;


    // =========================================================
    // 21 - CALCULATED
    // =========================================================

    private BigDecimal netTaxPayable;
}