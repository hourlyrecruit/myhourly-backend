package com.my_hourly.form_16.dto;

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