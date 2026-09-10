package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16Section16DeductionRequest;
import com.my_hourly.form_16.dto.Form16Section16DeductionResponse;
import com.my_hourly.form_16.service.Form16Section16DeductionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@Tag(
        name = "29 - Form 16 Section 16 Deduction",
        description = "Form 16 Section 16 Deduction APIs"
)
@RestController
@RequestMapping("/api/form16")
@RequiredArgsConstructor
public class Form16Section16DeductionController {

    private final Form16Section16DeductionService deductionService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping("/{form16Id}/section16-deduction")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16Section16DeductionResponse>
    createDeduction(
            @PathVariable Long form16Id,
            @RequestBody Form16Section16DeductionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        deductionService.createDeduction(
                                form16Id,
                                request
                        )
                );
    }


    // =========================================================
    // GET
    // =========================================================

    @GetMapping("/{form16Id}/section16-deduction")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<Form16Section16DeductionResponse>
    getDeduction(
            @PathVariable Long form16Id) {

        return ResponseEntity.ok(
                deductionService.getDeductionByForm16Id(
                        form16Id
                )
        );
    }


    // =========================================================
    // PUT
    // =========================================================

    @PutMapping("/{form16Id}/section16-deduction")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16Section16DeductionResponse>
    updateDeduction(
            @PathVariable Long form16Id,
            @RequestBody Form16Section16DeductionRequest request) {

        return ResponseEntity.ok(
                deductionService.updateDeduction(
                        form16Id,
                        request
                )
        );
    }


    // =========================================================
    // AUTO SAVE
    //
    // React calls this automatically.
    // User does NOT click Save.
    // =========================================================

    @PatchMapping("/{form16Id}/section16-deduction/auto-save")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16Section16DeductionResponse>
    autoSaveDeduction(
            @PathVariable Long form16Id,
            @RequestBody Form16Section16DeductionRequest request) {

        return ResponseEntity.ok(
                deductionService.autoSaveDeduction(
                        form16Id,
                        request
                )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{form16Id}/section16-deduction")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Void> deleteDeduction(
            @PathVariable Long form16Id) {

        deductionService.deleteDeduction(form16Id);

        return ResponseEntity
                .noContent()
                .build();
    }
}