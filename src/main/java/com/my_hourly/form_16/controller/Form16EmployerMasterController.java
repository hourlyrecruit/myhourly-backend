package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16EmployerMasterRequest;
import com.my_hourly.form_16.dto.Form16EmployerMasterResponse;
import com.my_hourly.form_16.service.Form16EmployerMasterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/form16/employer-master")
@RequiredArgsConstructor
@Tag(
        name = "22 - Form 16 Employeer Master",
        description = "Form 16 APIs"
)
public class Form16EmployerMasterController {


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Create Employer Master",
            description = "Creates a new employer master as inactive"
    )
    public ResponseEntity<Form16EmployerMasterResponse>
    createEmployerMaster(
            @RequestBody Form16EmployerMasterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createEmployerMaster(request)
                );
    }


    // =========================================================
    // GET ACTIVE
    // =========================================================

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'MANAGER')")
    @Operation(
            summary = "Get Active Employer Master"
    )
    public ResponseEntity<Form16EmployerMasterResponse>
    getActiveEmployerMaster() {

        return ResponseEntity.ok(
                service.getActiveEmployerMaster()
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'MANAGER')")
    @Operation(
            summary = "Get Employer Master By ID"
    )
    public ResponseEntity<Form16EmployerMasterResponse>
    getEmployerMasterById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getEmployerMasterById(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Update Employer Master"
    )
    public ResponseEntity<Form16EmployerMasterResponse>
    updateEmployerMaster(
            @PathVariable Long id,
            @RequestBody Form16EmployerMasterRequest request) {

        return ResponseEntity.ok(
                service.updateEmployerMaster(
                        id,
                        request
                )
        );
    }


    // =========================================================
    // ACTIVATE
    // =========================================================

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Activate Employer Master",
            description =
                    "Activates selected employer master and " +
                            "deactivates the previously active master"
    )
    public ResponseEntity<Form16EmployerMasterResponse>
    activateEmployerMaster(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.activateEmployerMaster(id)
        );
    }


    // =========================================================
    // DEACTIVATE
    // =========================================================

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Deactivate Employer Master"
    )
    public ResponseEntity<Form16EmployerMasterResponse>
    deactivateEmployerMaster(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.deactivateEmployerMaster(id)
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Delete Employer Master"
    )
    public ResponseEntity<Void>
    deleteEmployerMaster(
            @PathVariable Long id) {

        service.deleteEmployerMaster(id);

        return ResponseEntity.noContent().build();
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @Operation(
            summary = "Get All Employer Masters"
    )
    public ResponseEntity<List<Form16EmployerMasterResponse>>
    getAllEmployerMasters() {

        return ResponseEntity.ok(
                service.getAllEmployerMasters()
        );
    }


    // =========================================================
    // SERVICE
    // =========================================================

    private final Form16EmployerMasterService service;
}