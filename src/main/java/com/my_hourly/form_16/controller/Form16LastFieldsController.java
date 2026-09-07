package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16LastFieldsRequest;
import com.my_hourly.form_16.dto.Form16LastFieldsResponse;
import com.my_hourly.form_16.service.Form16LastFieldsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/form16")
@RequiredArgsConstructor
@Tag(
        name = "31 Form16 - Last Fields",
        description = "Form16 last fields and tax calculation APIs"
)
public class Form16LastFieldsController {


    private final Form16LastFieldsService lastFieldsService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping("/{form16Id}/last-fields")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Create Form16 Last Fields"
    )
    public ResponseEntity<Form16LastFieldsResponse> createLastFields(

            @PathVariable Long form16Id,

            @RequestBody Form16LastFieldsRequest request) {

        Form16LastFieldsResponse response =
                lastFieldsService.createLastFields(
                        form16Id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET
    // =========================================================

    @GetMapping("/{form16Id}/last-fields")
    @PreAuthorize(
            "hasAnyRole('EMPLOYEE', 'HR', 'MANAGER')"
    )
    @Operation(
            summary = "Get Form16 Last Fields"
    )
    public ResponseEntity<Form16LastFieldsResponse> getLastFields(

            @PathVariable Long form16Id) {

        return ResponseEntity.ok(
                lastFieldsService.getLastFields(
                        form16Id
                )
        );
    }


    // =========================================================
    // PUT
    // =========================================================

    @PutMapping("/{form16Id}/last-fields")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Update Form16 Last Fields"
    )
    public ResponseEntity<Form16LastFieldsResponse> updateLastFields(

            @PathVariable Long form16Id,

            @RequestBody Form16LastFieldsRequest request) {

        return ResponseEntity.ok(
                lastFieldsService.updateLastFields(
                        form16Id,
                        request
                )
        );
    }


    // =========================================================
    // PATCH - AUTO SAVE
    // =========================================================

    @PatchMapping("/{form16Id}/last-fields")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Auto Save Form16 Last Fields"
    )
    public ResponseEntity<Form16LastFieldsResponse> autoSaveLastFields(

            @PathVariable Long form16Id,

            @RequestBody Form16LastFieldsRequest request) {

        return ResponseEntity.ok(
                lastFieldsService.autoSaveLastFields(
                        form16Id,
                        request
                )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{form16Id}/last-fields")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Delete Form16 Last Fields"
    )
    public ResponseEntity<Void> deleteLastFields(

            @PathVariable Long form16Id) {

        lastFieldsService.deleteLastFields(
                form16Id
        );

        return ResponseEntity.noContent().build();
    }
}