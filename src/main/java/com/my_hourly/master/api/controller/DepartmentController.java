package com.my_hourly.master.api.controller;

import com.my_hourly.common.response.ApiResponse;
import com.my_hourly.common.response.PageResponse;
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
@Tag(name = "Department Controller", description = "ONLY SUPER_ADMIN, MANAGER, HR_ADMIN ARE ALLOWED")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('department:create')")
    @Operation(description = "Add Department like: Human Resources, Information Technology, Administration, Engineering etc.  ")
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
    @PreAuthorize("hasAuthority('department:view')")
    @Operation(description = "Get Department By ID")
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
    @PreAuthorize("hasAuthority('department:view')")
    @Operation(description = "Get All Departments")
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
    @PreAuthorize("hasAuthority('department:update')")
    @Operation(description = "Update Department")
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
    @PreAuthorize("hasAuthority('department:update')")
    @Operation(description = "Change Department Status:- True: Active, False: InActive")
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
    @PreAuthorize("hasAuthority('department:delete')")
    @Operation(description = "Delete Department BY ID")
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