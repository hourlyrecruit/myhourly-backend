package com.my_hourly.Form_16A.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16QuarterRequest {

    // =========================================================
    // Q1
    // =========================================================

    private BigDecimal q1AmountPaidCredited;

    private BigDecimal q1TaxDeducted;

    private BigDecimal q1TaxDepositedRemitted;


    // =========================================================
    // Q2
    // =========================================================

    private BigDecimal q2AmountPaidCredited;

    private BigDecimal q2TaxDeducted;

    private BigDecimal q2TaxDepositedRemitted;


    // =========================================================
    // Q3
    // =========================================================

    private BigDecimal q3AmountPaidCredited;

    private BigDecimal q3TaxDeducted;

    private BigDecimal q3TaxDepositedRemitted;


    // =========================================================
    // Q4
    // =========================================================

    private BigDecimal q4AmountPaidCredited;

    private BigDecimal q4TaxDeducted;

    private BigDecimal q4TaxDepositedRemitted;


    // =========================================================
    // BOOK ADJUSTMENT
    // =========================================================
    //
    // Serial Number       → AUTOMATIC
    // Form 24G Receipt    → AUTOMATIC
    // DDO Serial Number   → AUTOMATIC
    // Transfer Date       → AUTOMATIC
    //
    // Therefore these are NOT included in Request.
    // =========================================================

    private BigDecimal bookAdjustmentTaxDeposited;

    private String statusOfMatchingWithForm24G;
}