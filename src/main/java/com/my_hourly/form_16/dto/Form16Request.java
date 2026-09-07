package com.my_hourly.form_16.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Request {

    // =========================================================
    // EMPLOYEE ADDRESS
    // ENTERED ONLY FOR FIRST FORM 16
    // =========================================================

    private String employeeAddress;


    // =========================================================
    // THESE FIELDS ARE ACCEPTED IN REQUEST
    // BUT BACKEND CALCULATES THE VALUES AUTOMATICALLY
    // =========================================================

    private String assessmentYear;

    private LocalDate employmentFrom;

    private LocalDate employmentTo;


    // =========================================================
    // TAX REGIME
    // =========================================================

    private Boolean optingOutOfTaxation115BAC1A;
}