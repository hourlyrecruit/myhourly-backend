package com.my_hourly.Form_16A.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16ChallanRequest {

    // =========================================================
    // TAX DEPOSITED
    // =========================================================

    private BigDecimal taxDeposited;


    // =========================================================
    // BSR CODE OF BANK BRANCH
    // =========================================================

    private String bsrCode;


    // =========================================================
    // DATE ON WHICH TAX DEPOSITED
    // =========================================================

    private LocalDate taxDepositedDate;


    // =========================================================
    // CHALLAN SERIAL NUMBER
    // =========================================================

    private String challanSerialNumber;


    // =========================================================
    // STATUS OF MATCHING WITH OLTAS
    // =========================================================

    private String statusOfMatchingWithOltas;
}