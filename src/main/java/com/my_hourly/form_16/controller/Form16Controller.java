package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16Request;
import com.my_hourly.form_16.dto.Form16Response;
import com.my_hourly.form_16.service.Form16Service;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/form16")
@RequiredArgsConstructor
@Tag(
        name = "23 - Form 16",
        description = "Form 16 APIs"
)
public class Form16Controller {


    private final Form16Service form16Service;


    // =========================================================
    // CREATE FORM16
    // HR + MANAGER
    // =========================================================

    @PostMapping("/{employeeId}")
    @PreAuthorize(
            "hasAnyRole('HR', 'MANAGER')"
    )
    public ResponseEntity<Form16Response> createForm16(

            @PathVariable Long employeeId,

            @RequestBody Form16Request request

    ) {

        Form16Response response =
                form16Service.createForm16(
                        employeeId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET FORM16 BY ID
    // EMPLOYEE + HR + MANAGER
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('EMPLOYEE', 'HR', 'MANAGER')"
    )
    public ResponseEntity<Form16Response> getForm16(

            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                form16Service
                        .getForm16ForLoggedInEmployee(
                                id
                        )
        );
    }


    // =========================================================
    // GET FORM16 BY EMPLOYEE + ASSESSMENT YEAR
    // HR + MANAGER
    // =========================================================

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize(
            "hasAnyRole('HR', 'MANAGER')"
    )
    public ResponseEntity<Form16Response>
    getForm16ByEmployeeAndAssessmentYear(

            @PathVariable Long employeeId,

            @RequestParam String assessmentYear

    ) {

        return ResponseEntity.ok(
                form16Service
                        .getForm16ByEmployeeAndAssessmentYear(
                                employeeId,
                                assessmentYear
                        )
        );
    }


    // =========================================================
    // ACTIVATE ONE EMPLOYEE FORM16
    // HR + MANAGER
    // =========================================================

    @PatchMapping(
            "/employee/{employeeId}/activate"
    )
    @PreAuthorize(
            "hasAnyRole('HR', 'MANAGER')"
    )
    public ResponseEntity<String>
    activateEmployeeForm16(

            @PathVariable Long employeeId

    ) {

        form16Service
                .activateEmployeeForm16(
                        employeeId
                );

        return ResponseEntity.ok(
                "Form 16 activated successfully for Employee ID: "
                        + employeeId
        );
    }


    // =========================================================
    // ACTIVATE ALL FORM16
    // HR + MANAGER
    // =========================================================

    @PatchMapping("/activate-all")
    @PreAuthorize(
            "hasAnyRole('HR', 'MANAGER')"
    )
    public ResponseEntity<String>
    activateAllForm16() {

        form16Service
                .activateAllForm16();

        return ResponseEntity.ok(
                "All Form 16 records activated successfully."
        );
    }


    // =========================================================
    // DEACTIVATE ONE EMPLOYEE FORM16
    // HR + MANAGER
    // =========================================================

    @PatchMapping(
            "/employee/{employeeId}/deactivate"
    )
    @PreAuthorize(
            "hasAnyRole('HR', 'MANAGER')"
    )
    public ResponseEntity<String>
    deactivateEmployeeForm16(

            @PathVariable Long employeeId

    ) {

        form16Service
                .deactivateEmployeeForm16(
                        employeeId
                );

        return ResponseEntity.ok(
                "Form 16 deactivated successfully for Employee ID: "
                        + employeeId
        );
    }


    // =========================================================
    // DELETE ONE EMPLOYEE FORM16
    // HR + MANAGER
    // =========================================================

    @DeleteMapping(
            "/employee/{employeeId}"
    )
    @PreAuthorize(
            "hasAnyRole('HR', 'MANAGER')"
    )
    public ResponseEntity<String>
    deleteEmployeeForm16(

            @PathVariable Long employeeId

    ) {

        form16Service
                .deleteEmployeeForm16(
                        employeeId
                );

        return ResponseEntity.ok(
                "Form 16 deleted successfully for Employee ID: "
                        + employeeId
        );
    }
}