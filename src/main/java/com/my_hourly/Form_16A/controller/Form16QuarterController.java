package com.my_hourly.Form_16A.controller;

import com.my_hourly.Form_16A.dto.Form16QuarterRequest;
import com.my_hourly.Form_16A.dto.Form16QuarterResponse;
import com.my_hourly.Form_16A.service.Form16QuarterService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;


@Tag(
        name = "24 - Form 16 Quarterly Details",
        description = "Form 16 Part A Quarterly TDS Details APIs"
)
@RestController
@RequestMapping("/api/v1/form16")
@RequiredArgsConstructor
public class Form16QuarterController {


    private final Form16QuarterService form16QuarterService;


    // =========================================================
    // CREATE
    // HR + MANAGER ONLY
    // =========================================================

    @PostMapping("/{form16Id}/quarter")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Form16QuarterResponse> createQuarter(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16QuarterRequest request) {

        Form16QuarterResponse response =
                form16QuarterService.createQuarter(
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

    @GetMapping("/{form16Id}/quarter")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<Form16QuarterResponse> getQuarter(
            @PathVariable Long form16Id) {

        Form16QuarterResponse response =
                form16QuarterService
                        .getQuarterByForm16Id(
                                form16Id
                        );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // UPDATE
    // HR + MANAGER ONLY
    // =========================================================

    @PutMapping("/{form16Id}/quarter")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Form16QuarterResponse> updateQuarter(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16QuarterRequest request) {

        Form16QuarterResponse response =
                form16QuarterService.updateQuarter(
                        form16Id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // DELETE
    // HR + MANAGER ONLY
    // =========================================================

    @DeleteMapping("/{form16Id}/quarter")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Void> deleteQuarter(
            @PathVariable Long form16Id) {

        form16QuarterService.deleteQuarter(
                form16Id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}