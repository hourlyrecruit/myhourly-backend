//package com.my_hourly.form_16.controller;

package com.my_hourly.Form_16A.controller;

import com.my_hourly.form_16.dto.Form16VerificationRequest;
import com.my_hourly.form_16.dto.Form16VerificationResponse;
import com.my_hourly.form_16.service.Form16VerificationService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@Tag(
        name = "26 - Form 16 Verification",
        description = "Form 16 Verification Details APIs"
)
@RestController
@RequestMapping("/api/v1/form16")
@RequiredArgsConstructor
public class Form16VerificationController {


    private final Form16VerificationService form16VerificationService;


    // =========================================================
    // CREATE VERIFICATION
    //
    // HR + MANAGER ONLY
    //
    // POST
    // /api/v1/form16/{form16Id}/verification
    // =========================================================

    @PostMapping("/{form16Id}/verification")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Form16VerificationResponse> createVerification(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16VerificationRequest request) {

        Form16VerificationResponse response =
                form16VerificationService.createVerification(
                        form16Id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET VERIFICATION
    //
    // EMPLOYEE + MANAGER + HR
    //
    // GET
    // /api/v1/form16/{form16Id}/verification
    // =========================================================

    @GetMapping("/{form16Id}/verification")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<Form16VerificationResponse> getVerification(
            @PathVariable Long form16Id) {

        Form16VerificationResponse response =
                form16VerificationService
                        .getVerificationByForm16Id(
                                form16Id
                        );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // UPDATE VERIFICATION
    //
    // HR + MANAGER ONLY
    //
    // PUT
    // /api/v1/form16/{form16Id}/verification
    // =========================================================

    @PutMapping("/{form16Id}/verification")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Form16VerificationResponse> updateVerification(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16VerificationRequest request) {

        Form16VerificationResponse response =
                form16VerificationService.updateVerification(
                        form16Id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // DELETE VERIFICATION
    //
    // HR + MANAGER ONLY
    //
    // DELETE
    // /api/v1/form16/{form16Id}/verification
    // =========================================================

    @DeleteMapping("/{form16Id}/verification")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Void> deleteVerification(
            @PathVariable Long form16Id) {

        form16VerificationService.deleteVerification(
                form16Id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}