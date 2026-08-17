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
    @Operation(summary = "Create Initial Salary Structure, 'SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> create(
            @Valid @RequestBody CreateSalaryStructureRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaryStructureService.create(request));
    }

    @PostMapping("/revision")
    @Operation(summary = "Create Salary Revision, This marks the prior structure INACTIVE and sets its effectiveTo to one day before the new effectiveFrom., 'SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> createRevision(
            @Valid @RequestBody CreateSalaryStructureRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaryStructureService.createRevision(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Salary Structure By Id, 'SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<SalaryStructureResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                salaryStructureService.getById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get Salary Structures of Employee, 'SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<List<SalaryStructureResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {

        return ResponseEntity.ok(
                salaryStructureService.getByEmployee(employeeId, activeOnly));
    }

    @GetMapping
    @Operation(summary = "Get All Salary Structures, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<List<SalaryStructureResponse>> getAll(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {

        return ResponseEntity.ok(
                salaryStructureService.getAll(activeOnly));
    }
}
