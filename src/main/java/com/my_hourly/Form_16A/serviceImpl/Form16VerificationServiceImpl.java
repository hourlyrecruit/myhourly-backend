package com.my_hourly.form_16.service.impl;

import com.my_hourly.form_16.dto.Form16VerificationRequest;
import com.my_hourly.form_16.dto.Form16VerificationResponse;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16Verification;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.repository.Form16VerificationRepository;
import com.my_hourly.form_16.service.Form16VerificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16VerificationServiceImpl
        implements Form16VerificationService {

    private final Form16VerificationRepository verificationRepository;

    private final Form16Repository form16Repository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16VerificationResponse createVerification(
            Long form16Id,
            Form16VerificationRequest request) {

        // -----------------------------------------------------
        // Find Form 16
        // -----------------------------------------------------

        Form16 form16 = form16Repository.findById(form16Id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Form 16 not found with id: " + form16Id
                        )
                );


        // -----------------------------------------------------
        // Check whether verification already exists
        // -----------------------------------------------------

        if (verificationRepository.existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Verification already exists for Form 16 id: "
                            + form16Id
            );
        }


        // -----------------------------------------------------
        // Create verification
        // -----------------------------------------------------

        Form16Verification verification =
                new Form16Verification();

        verification.setForm16(form16);

        verification.setPlace(request.getPlace());

        verification.setDesignation(
                request.getDesignation()
        );

        verification.setFullName(
                request.getFullName()
        );

        verification.setSignature(
                request.getSignature()
        );


        // -----------------------------------------------------
        // AUTOMATIC VERIFICATION DATE
        // -----------------------------------------------------

        verification.setDate(LocalDate.now());


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        Form16Verification saved =
                verificationRepository.save(verification);


        return convertToResponse(saved);
    }


    // =========================================================
    // GET BY FORM 16 ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16VerificationResponse getVerificationByForm16Id(
            Long form16Id) {

        Form16Verification verification =
                verificationRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Verification not found for Form 16 id: "
                                                + form16Id
                                )
                        );


        return convertToResponse(verification);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16VerificationResponse updateVerification(
            Long form16Id,
            Form16VerificationRequest request) {

        // -----------------------------------------------------
        // Find existing verification
        // -----------------------------------------------------

        Form16Verification verification =
                verificationRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Verification not found for Form 16 id: "
                                                + form16Id
                                )
                        );


        // -----------------------------------------------------
        // Update fields
        // -----------------------------------------------------

        verification.setPlace(
                request.getPlace()
        );

        verification.setDesignation(
                request.getDesignation()
        );

        verification.setFullName(
                request.getFullName()
        );

        verification.setSignature(
                request.getSignature()
        );


        // -----------------------------------------------------
        // AUTOMATICALLY UPDATE VERIFICATION DATE
        // -----------------------------------------------------

        verification.setDate(LocalDate.now());


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        Form16Verification updated =
                verificationRepository.save(verification);


        return convertToResponse(updated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteVerification(
            Long form16Id) {

        // -----------------------------------------------------
        // Check exists
        // -----------------------------------------------------

        if (!verificationRepository.existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Verification not found for Form 16 id: "
                            + form16Id
            );
        }


        // -----------------------------------------------------
        // Delete
        // -----------------------------------------------------

        verificationRepository.deleteByForm16Id(form16Id);
    }


    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private Form16VerificationResponse convertToResponse(
            Form16Verification verification) {

        return Form16VerificationResponse.builder()

                .id(verification.getId())

                .form16Id(
                        verification.getForm16().getId()
                )

                .place(
                        verification.getPlace()
                )

                .date(
                        verification.getDate()
                )

                .designation(
                        verification.getDesignation()
                )

                .fullName(
                        verification.getFullName()
                )

                .signature(
                        verification.getSignature()
                )

                .build();
    }
}