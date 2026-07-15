package com.my_hourly.employee.controller;

import com.my_hourly.common.response.ApiResponse;
import com.my_hourly.common.response.PageResponse;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeDropdownResponse;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAuthority('employee:create')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request) {

        EmployeeResponse response = employeeService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee created successfully.")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:update')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @Valid @RequestBody UpdateEmployeeRequest request) {

        EmployeeResponse response = employeeService.update(request);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee updated successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:view')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(
            @PathVariable Long id) {

        EmployeeResponse response = employeeService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee fetched successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('employee:view')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "employeeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        PageResponse<EmployeeResponse> response =
                employeeService.getAll(
                        page,
                        size,
                        search,
                        sortBy,
                        sortDirection
                );

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<EmployeeResponse>>builder()
                        .success(true)
                        .message("Employees fetched successfully.")
                        .data(response)
                        .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('employee:update')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        employeeService.changeStatus(id, active);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Employee status updated successfully.")
                        .build());
    }

    @GetMapping("/dropdown")
    @PreAuthorize("hasAuthority('employee:view')")
    public ResponseEntity<ApiResponse<List<EmployeeDropdownResponse>>> getDropdown() {

        return ResponseEntity.ok(
                ApiResponse.<List<EmployeeDropdownResponse>>builder()
                        .success(true)
                        .message("Employees fetched successfully.")
                        .data(employeeService.getDropdown())
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMyProfile() {

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Profile fetched successfully.")
                        .data(employeeService.getMyProfile())
                        .build()
        );
    }

}