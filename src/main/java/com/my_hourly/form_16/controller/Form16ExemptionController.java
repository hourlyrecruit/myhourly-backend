package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16ExemptionRequest;
import com.my_hourly.form_16.dto.Form16ExemptionResponse;
import com.my_hourly.form_16.service.Form16ExemptionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@Tag(
        name = "28 - Form 16 Exemption",
        description = "Form 16 Exemption APIs"
)
@RestController
@RequestMapping("/api/form16")
@RequiredArgsConstructor
public class Form16ExemptionController {


    private final Form16ExemptionService
            form16ExemptionService;


    // =========================================================
    // CREATE
    // HR + MANAGER
    // =========================================================

    @PostMapping("/{form16Id}/exemption")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16ExemptionResponse>
    createExemption(
            @PathVariable Long form16Id,
            @RequestBody Form16ExemptionRequest request) {


        Form16ExemptionResponse response =
                form16ExemptionService
                        .createExemption(
                                form16Id,
                                request
                        );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET
    // EMPLOYEE + MANAGER + HR
    // =========================================================

    @GetMapping("/{form16Id}/exemption")
    @PreAuthorize(
            "hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')"
    )
    public ResponseEntity<Form16ExemptionResponse>
    getExemption(
            @PathVariable Long form16Id) {


        Form16ExemptionResponse response =
                form16ExemptionService
                        .getExemptionByForm16Id(
                                form16Id
                        );


        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // UPDATE
    // HR + MANAGER
    // =========================================================

    @PutMapping("/{form16Id}/exemption")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16ExemptionResponse>
    updateExemption(
            @PathVariable Long form16Id,
            @RequestBody Form16ExemptionRequest request) {


        Form16ExemptionResponse response =
                form16ExemptionService
                        .updateExemption(
                                form16Id,
                                request
                        );


        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // DELETE
    // HR + MANAGER
    // =========================================================

    @DeleteMapping("/{form16Id}/exemption")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Void> deleteExemption(
            @PathVariable Long form16Id) {


        form16ExemptionService
                .deleteExemption(
                        form16Id
                );


        return ResponseEntity
                .noContent()
                .build();
    }
}