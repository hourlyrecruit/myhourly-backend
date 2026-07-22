package com.my_hourly.employee.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeByEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeDropdownResponse;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "03-Employee Profile", description = "Create profile, update, Fetch etc.")
public class EmployeeController {

    private final EmployeeService employeeService;
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('employee:create')")
//    @Operation(summary = "Create employees profile", description = "Access by Employee")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            content = @Content(
//                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
//                    encoding = {
//                            @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE),
//                            @Encoding(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//                    }
//            )
//    )
//    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
//            @Valid @RequestPart("request") CreateEmployeeRequest request,
//            @RequestPart(value = "file", required = false) MultipartFile file) {
//
//        EmployeeResponse response = employeeService.create(request, file);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.<EmployeeResponse>builder()
//                        .success(true)
//                        .message("Employee created successfully.")
//                        .data(response)
//                        .build());
//    }

    @PutMapping(
            value = "/profile-photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Change Profile Photo: image/jpeg, image/jpg, image/png. Access: EMPLOYEE")
    public ResponseEntity<ApiResponse<EmployeeResponse>> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file) {

        EmployeeResponse response = employeeService.updateProfilePhoto(file);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Profile photo uploaded successfully.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Update employee profile. Access: EMPLOYEE")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @Valid @RequestBody UpdateEmployeeByEmployeeRequest request) {

        EmployeeResponse response = employeeService.update(request);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee updated successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee BY ID. Access: 'HR_ADMIN', 'MANAGER'", description = "HR_ADMIN', 'MANAGER'")
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
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employees. Access: 'HR_ADMIN', 'MANAGER", description = "Only Access by 'HR_ADMIN', 'MANAGER'")
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
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Change the employee Profile Status: true=Active or false=Inactive. Access: 'HR_ADMIN', 'MANAGER'", description = "Only Access 'HR_ADMIN', 'MANAGER'")
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
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Get All Employees ID and Name. Access: 'HR_ADMIN', 'MANAGER')", description = "Access by 'HR_ADMIN', 'MANAGER'")
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
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get Logged in employee's profile. Access: EMPLOYEE")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMyProfile() {

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Profile fetched successfully.")
                        .data(employeeService.getMyProfile())
                        .build()
        );
    }



//    @GetMapping("/{id}/profile-photo")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<byte[]> getProfilePhoto(
//            @PathVariable Long id) {
//
//        Employee employee = employeeService.getEmployee(id);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(employee.getProfilePhotoType()))
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "inline; filename=\"" + employee.getProfilePhotoName() + "\""
//                )
//                .body(employee.getProfilePhoto());
//    }

}