package com.my_hourly.payroll.controller;

import com.my_hourly.payroll.dto.request.CreateEmployeePaymentDetailsRequest;
import com.my_hourly.payroll.dto.request.UpdateEmployeePaymentDetailsRequest;
import com.my_hourly.payroll.dto.response.EmployeePaymentDetailsResponse;
import com.my_hourly.payroll.service.EmployeePaymentDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payroll/payment-details")
@Tag(name = "Employee Payment Details", description = "Employee Payment Details Management APIs")
public class EmployeePaymentDetailsController {

    private final EmployeePaymentDetailsService paymentDetailsService;

    @PostMapping
    @Operation(summary = "Create Employee Payment Details")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EmployeePaymentDetailsResponse> create(
            @Valid @RequestBody CreateEmployeePaymentDetailsRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentDetailsService.create(request));
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update Employee Payment Details")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EmployeePaymentDetailsResponse> update(
            @PathVariable Long employeeId,
            @Valid @RequestBody UpdateEmployeePaymentDetailsRequest request) {

        return ResponseEntity.ok(
                paymentDetailsService.update(employeeId, request));
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get Employee Payment Details")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EmployeePaymentDetailsResponse> getByEmployeeId(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                paymentDetailsService.getByEmployeeId(employeeId));
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete Employee Payment Details")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long employeeId) {

        paymentDetailsService.delete(employeeId);

        return ResponseEntity.noContent().build();
    }

}
