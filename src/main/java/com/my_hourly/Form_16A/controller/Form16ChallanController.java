package com.my_hourly.Form_16A.controller;

import com.my_hourly.Form_16A.dto.Form16ChallanListResponse;
import com.my_hourly.Form_16A.dto.Form16ChallanRequest;
import com.my_hourly.Form_16A.dto.Form16ChallanResponse;
import com.my_hourly.Form_16A.service.Form16ChallanService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/form16")
@RequiredArgsConstructor
@Tag(
        name = "25 Form 16 - Challan",
        description =
                "Form 16 Tax Deducted and Deposited Through Challan APIs"
)
public class Form16ChallanController {


    private final Form16ChallanService challanService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping("/{form16Id}/challan")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Form16ChallanResponse> createChallan(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16ChallanRequest request) {

        Form16ChallanResponse response =
                challanService.createChallan(
                        form16Id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping("/{form16Id}/challan")
    @PreAuthorize(
            "hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')"
    )
    public ResponseEntity<Form16ChallanListResponse> getAllChallans(
            @PathVariable Long form16Id) {

        return ResponseEntity.ok(
                challanService.getAllChallans(
                        form16Id
                )
        );
    }


    // =========================================================
    // GET SINGLE
    // =========================================================

    @GetMapping("/{form16Id}/challan/{challanId}")
    @PreAuthorize(
            "hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')"
    )
    public ResponseEntity<Form16ChallanResponse> getChallan(
            @PathVariable Long form16Id,
            @PathVariable Long challanId) {

        return ResponseEntity.ok(
                challanService.getChallan(
                        form16Id,
                        challanId
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{form16Id}/challan/{challanId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Form16ChallanResponse> updateChallan(
            @PathVariable Long form16Id,
            @PathVariable Long challanId,
            @Valid @RequestBody Form16ChallanRequest request) {

        Form16ChallanResponse response =
                challanService.updateChallan(
                        form16Id,
                        challanId,
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // DELETE SINGLE
    // =========================================================

    @DeleteMapping("/{form16Id}/challan/{challanId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Void> deleteChallan(
            @PathVariable Long form16Id,
            @PathVariable Long challanId) {

        challanService.deleteChallan(
                form16Id,
                challanId
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    // =========================================================
    // DELETE ALL
    // =========================================================

    @DeleteMapping("/{form16Id}/challan")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Void> deleteAllChallans(
            @PathVariable Long form16Id) {

        challanService.deleteAllChallans(
                form16Id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}