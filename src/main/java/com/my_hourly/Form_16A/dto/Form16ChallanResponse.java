package com.my_hourly.Form_16A.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16ChallanResponse {

    // =========================================================
    // ID
    // =========================================================

    private Long id;


    // =========================================================
    // SL. NO.
    // =========================================================

    private Integer serialNumber;


    // =========================================================
    // TAX DEPOSITED
    // =========================================================

    private BigDecimal taxDeposited;


    // =========================================================
    // BSR CODE
    // =========================================================

    private String bsrCode;


    // =========================================================
    // TAX DEPOSITED DATE
    // =========================================================

    private LocalDate taxDepositedDate;


    // =========================================================
    // CHALLAN SERIAL NUMBER
    // =========================================================

    private String challanSerialNumber;


    // =========================================================
    // OLTAS MATCHING STATUS
    // =========================================================

    private String statusOfMatchingWithOltas;
}