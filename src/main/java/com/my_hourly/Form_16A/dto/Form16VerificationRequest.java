package com.my_hourly.Form_16A.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16VerificationRequest {

    // =========================================================
    // PLACE
    // =========================================================

    private String place;


    // =========================================================
    // DESIGNATION
    // =========================================================

    private String designation;


    // =========================================================
    // FULL NAME
    // =========================================================

    private String fullName;


    // =========================================================
    // SIGNATURE - TEXT
    // =========================================================

    private String signature;
}