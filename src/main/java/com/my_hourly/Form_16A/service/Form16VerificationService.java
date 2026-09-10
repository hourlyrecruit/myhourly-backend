package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16VerificationRequest;
import com.my_hourly.form_16.dto.Form16VerificationResponse;

public interface Form16VerificationService {

    // =========================================================
    // CREATE
    // =========================================================
    //
    // Creates verification details for a Form 16.
    //
    // Verification date will be generated automatically
    // using LocalDate.now().
    //
    // =========================================================

    Form16VerificationResponse createVerification(
            Long form16Id,
            Form16VerificationRequest request
    );


    // =========================================================
    // GET BY FORM 16 ID
    // =========================================================

    Form16VerificationResponse getVerificationByForm16Id(
            Long form16Id
    );


    // =========================================================
    // UPDATE
    // =========================================================
    //
    // Place, designation, full name and signature
    // can be changed.
    //
    // Verification date will be automatically updated
    // to the current date.
    //
    // =========================================================

    Form16VerificationResponse updateVerification(
            Long form16Id,
            Form16VerificationRequest request
    );


    // =========================================================
    // DELETE
    // =========================================================

    void deleteVerification(
            Long form16Id
    );
}