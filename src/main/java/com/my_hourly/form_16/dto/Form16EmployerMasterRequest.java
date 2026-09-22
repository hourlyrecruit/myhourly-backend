package com.my_hourly.form_16.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16EmployerMasterRequest {


    // =========================================================
    // EMPLOYER NAME
    // =========================================================

    @NotBlank(
            message = "Employer name is required"
    )
    @Size(
            max = 200,
            message = "Employer name cannot exceed 200 characters"
    )
    private String employerName;


    // =========================================================
    // EMPLOYER ADDRESS
    // =========================================================

    @NotBlank(
            message = "Employer address is required"
    )
    @Size(
            max = 2000,
            message = "Employer address cannot exceed 2000 characters"
    )
    private String employerAddress;


    // =========================================================
    // EMPLOYER PHONE
    // =========================================================

    @Size(
            max = 20,
            message = "Employer phone cannot exceed 20 characters"
    )
    private String employerPhone;


    // =========================================================
    // EMPLOYER EMAIL
    // =========================================================

    @Email(
            message = "Invalid employer email format"
    )
    @Size(
            max = 150,
            message = "Employer email cannot exceed 150 characters"
    )
    private String employerEmail;


    // =========================================================
    // DEDUCTOR PAN
    // =========================================================
    /*
     * PAN format:
     *
     * ABCDE1234F
     *
     * 5 alphabets
     * 4 numbers
     * 1 alphabet
     */

    @NotBlank(
            message = "Deductor PAN is required"
    )
    @Pattern(
            regexp = "^[A-Za-z]{5}[0-9]{4}[A-Za-z]$",
            message = "Invalid Deductor PAN. Expected format: ABCDE1234F"
    )
    private String deductorPan;


    // =========================================================
    // DEDUCTOR TAN
    // =========================================================
    /*
     * TAN format:
     *
     * ABCD12345E
     *
     * 4 alphabets
     * 5 numbers
     * 1 alphabet
     */

    @NotBlank(
            message = "Deductor TAN is required"
    )
    @Pattern(
            regexp = "^[A-Za-z]{4}[0-9]{5}[A-Za-z]$",
            message = "Invalid Deductor TAN. Expected format: ABCD12345E"
    )
    private String deductorTan;


    // =========================================================
    // CIT TDS ADDRESS
    // =========================================================

    @NotBlank(
            message = "CIT TDS address is required"
    )
    @Size(
            max = 2000,
            message = "CIT TDS address cannot exceed 2000 characters"
    )
    private String citTdsAddress;
}
