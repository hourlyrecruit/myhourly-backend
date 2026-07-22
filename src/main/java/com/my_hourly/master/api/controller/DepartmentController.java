package com.my_hourly.master.api.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.master.api.request.CreateDepartmentRequest;
import com.my_hourly.master.api.request.UpdateDepartmentRequest;
import com.my_hourly.master.api.response.DepartmentResponse;
import com.my_hourly.master.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "0-Department Controller", description = "ONLY SUPER_ADMIN, MANAGER, HR_ADMIN ARE ALLOWED")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Add Department like: Human Resources, Information Technology, Administration, Engineering etc. Access: 'HR_ADMIN', 'MANAGER'")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(
            @Valid @RequestBody CreateDepartmentRequest request) {

        DepartmentResponse response = departmentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DepartmentResponse>builder()
                        .success(true)
                        .message("Department created successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Get Department By ID. Access: 'HR_ADMIN', 'MANAGER'")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<DepartmentResponse>builder()
                        .success(true)
                        .message("Department fetched successfully.")
                        .data(departmentService.getById(id))
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Get All Departments. Access: 'HR_ADMIN', 'MANAGER'")
    public ResponseEntity<ApiResponse<PageResponse<DepartmentResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "departmentName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<DepartmentResponse>>builder()
                        .success(true)
                        .message("Departments fetched successfully.")
                        .data(departmentService.getAll(page, size, search, sortBy, sortDirection))
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Update Department. Access: 'HR_ADMIN', 'MANAGER'")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {

        return ResponseEntity.ok(
                ApiResponse.<DepartmentResponse>builder()
                        .success(true)
                        .message("Department updated successfully.")
                        .data(departmentService.update(id, request))
                        .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Change Department Status:- True: Active, False: InActive. Access: 'HR_ADMIN', 'MANAGER'")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        departmentService.changeStatus(id, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Department status updated successfully.")
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Department BY ID. Access: 'HR_ADMIN', 'MANAGER'")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        departmentService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Department deleted successfully.")
                        .build());
    }

}