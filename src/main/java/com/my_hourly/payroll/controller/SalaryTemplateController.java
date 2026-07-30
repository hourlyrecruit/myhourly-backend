package com.my_hourly.payroll.controller;

import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.payroll.dto.request.CreateSalaryTemplateRequest;
import com.my_hourly.payroll.dto.request.UpdateSalaryTemplateRequest;
import com.my_hourly.payroll.dto.response.SalaryTemplateResponse;
import com.my_hourly.payroll.service.SalaryTemplateService;
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
@RequestMapping("/api/v1/payroll/salary-templates")
@RequiredArgsConstructor
@Tag(name = "18-Salary Template", description = "Salary Template Management APIs")
public class SalaryTemplateController {

    private final SalaryTemplateService salaryTemplateService;

    @PostMapping
    @Operation(summary = "Create Salary Template")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<SalaryTemplateResponse> create(
            @Valid @RequestBody CreateSalaryTemplateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaryTemplateService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Salary Template")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<SalaryTemplateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSalaryTemplateRequest request) {

        return ResponseEntity.ok(
                salaryTemplateService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Salary Template By Id")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<SalaryTemplateResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                salaryTemplateService.getById(id));
    }

    @GetMapping("/employee-type/{employeeType}")
    @Operation(summary = "Get Salary Template By Employee Type")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<SalaryTemplateResponse> getByEmployeeType(
            @PathVariable EmploymentType employeeType) {

        return ResponseEntity.ok(
                salaryTemplateService.getByEmployeeType(employeeType));
    }

    @GetMapping
    @Operation(summary = "Get All Salary Templates")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<List<SalaryTemplateResponse>> getAll() {

        return ResponseEntity.ok(
                salaryTemplateService.getAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get All Active Salary Templates")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<List<SalaryTemplateResponse>> getAllActive() {

        return ResponseEntity.ok(
                salaryTemplateService.getAllActive());
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate Salary Template")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<Void> activate(
            @PathVariable Long id) {

        salaryTemplateService.activate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate Salary Template")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {

        salaryTemplateService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

}
