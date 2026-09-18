package com.my_hourly.Form_16A.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16ChallanListResponse {

    // =========================================================
    // CHALLAN ROWS
    // =========================================================

    private List<Form16ChallanResponse> challans;


    // =========================================================
    // TOTAL (Rs.)
    // =========================================================

    private BigDecimal totalTaxDeposited;
}