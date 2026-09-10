package com.my_hourly.Form_16A.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16QuarterResponse {

    // =========================================================
    // ID
    // =========================================================

    private Long id;


    // =========================================================
    // Q1
    // =========================================================

    private String q1ReceiptNumber;

    private BigDecimal q1AmountPaidCredited;

    private BigDecimal q1TaxDeducted;

    private BigDecimal q1TaxDepositedRemitted;


    // =========================================================
    // Q2
    // =========================================================

    private String q2ReceiptNumber;

    private BigDecimal q2AmountPaidCredited;

    private BigDecimal q2TaxDeducted;

    private BigDecimal q2TaxDepositedRemitted;


    // =========================================================
    // Q3
    // =========================================================

    private String q3ReceiptNumber;

    private BigDecimal q3AmountPaidCredited;

    private BigDecimal q3TaxDeducted;

    private BigDecimal q3TaxDepositedRemitted;


    // =========================================================
    // Q4
    // =========================================================

    private String q4ReceiptNumber;

    private BigDecimal q4AmountPaidCredited;

    private BigDecimal q4TaxDeducted;

    private BigDecimal q4TaxDepositedRemitted;


    // =========================================================
    // TOTAL
    // =========================================================

    private BigDecimal totalAmountPaidCredited;

    private BigDecimal totalTaxDeducted;

    private BigDecimal totalTaxDepositedRemitted;


    // =========================================================
    // BOOK ADJUSTMENT
    // =========================================================

    private Integer bookAdjustmentSerialNumber;

    private BigDecimal bookAdjustmentTaxDeposited;

    private String receiptNumberForm24G;

    private String ddoSerialNumberForm24G;

    private LocalDate dateOfTransferVoucher;

    private String statusOfMatchingWithForm24G;


    // =========================================================
    // TOTAL BOOK ADJUSTMENT
    // =========================================================

    private BigDecimal totalBookAdjustmentTaxDeposited;
}