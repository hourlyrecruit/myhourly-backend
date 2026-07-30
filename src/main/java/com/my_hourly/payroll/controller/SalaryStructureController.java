package com.my_hourly.payroll.controller;

import com.my_hourly.payroll.dto.request.CreateSalaryStructureRequest;
import com.my_hourly.payroll.dto.response.SalaryStructureResponse;
import com.my_hourly.payroll.service.SalaryStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll/salary-structures")
@RequiredArgsConstructor
@Tag(name = "19-Salary Structure", description = "Salary Structure Management APIs")
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    @PostMapping
    @Operation(summary = "Create Initial Salary Structure")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> create(
            @Valid @RequestBody CreateSalaryStructureRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaryStructureService.create(request));
    }

    @PostMapping("/revision")
    @Operation(summary = "Create Salary Revision")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> createRevision(
            @Valid @RequestBody CreateSalaryStructureRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaryStructureService.createRevision(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Salary Structure By Id")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                salaryStructureService.getById(id));
    }

    @GetMapping("/employee/{employeeId}/active")
    @Operation(summary = "Get Active Salary Structure")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> getActiveSalary(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                salaryStructureService.getActiveByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/history")
    @Operation(summary = "Get Salary History")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<List<SalaryStructureResponse>> getHistory(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                salaryStructureService.getHistory(employeeId));
    }

    @GetMapping("/active")
    @Operation(summary = "Get All Active Salary Structures")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<List<SalaryStructureResponse>> getAllActive() {

        return ResponseEntity.ok(
                salaryStructureService.getAllActive());
    }
}
