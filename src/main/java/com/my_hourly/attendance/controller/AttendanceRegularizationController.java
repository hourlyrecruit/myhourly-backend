package com.my_hourly.attendance.controller;

import com.my_hourly.attendance.api.request.CreateRegularizationRequest;
import com.my_hourly.attendance.api.request.RegularizationDetailActionRequest;
import com.my_hourly.attendance.api.response.RegularizationResponse;
import com.my_hourly.attendance.service.AttendanceRegularizationService;
import com.my_hourly.common.payload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance-regularizations")
@RequiredArgsConstructor
@Tag(name = "05-Attendance Regularization", description = "Attendance Regularization APIs")
public class AttendanceRegularizationController {

    private final AttendanceRegularizationService regularizationService;

    /* =================== Employee APIs =================== */

    @Operation(summary = "Create a regularization request. Access: EMPLOYEE, MANAGER, HR_ADMIN")
    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RegularizationResponse>> createRegularization(
            @Valid @RequestBody CreateRegularizationRequest request
    ) {
        RegularizationResponse response = regularizationService.createRegularization(request);

        return ResponseEntity.ok(
                ApiResponse.<RegularizationResponse>builder()
                        .success(true)
                        .message("Regularization request created successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Get my regularization requests. Access: EMPLOYEE, MANAGER, HR_ADMIN")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<RegularizationResponse>>> getMyRegularizations() {
        List<RegularizationResponse> response = regularizationService.getMyRegularizations();

        return ResponseEntity.ok(
                ApiResponse.<List<RegularizationResponse>>builder()
                        .success(true)
                        .message("Regularization requests fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /* =================== Manager APIs =================== */

    @Operation(summary = "Get pending regularization requests for manager. Access: MANAGER, HR_ADMIN")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<RegularizationResponse>>> getPendingForManager() {
        List<RegularizationResponse> response =
                regularizationService.getPendingRegularizationsForManager();

        return ResponseEntity.ok(
                ApiResponse.<List<RegularizationResponse>>builder()
                        .success(true)
                        .message("Pending regularization requests fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Get all regularization requests for manager's subordinates. Access: MANAGER, HR_ADMIN")
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<RegularizationResponse>>> getAllForManager() {
        List<RegularizationResponse> response =
                regularizationService.getAllRegularizationsForManager();

        return ResponseEntity.ok(
                ApiResponse.<List<RegularizationResponse>>builder()
                        .success(true)
                        .message("Regularization requests fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Get regularization details. Access: EMPLOYEE, MANAGER, HR_ADMIN")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RegularizationResponse>> getRegularizationById(
            @PathVariable Long id
    ) {
        RegularizationResponse response = regularizationService.getRegularizationById(id);

        return ResponseEntity.ok(
                ApiResponse.<RegularizationResponse>builder()
                        .success(true)
                        .message("Regularization details fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Approve or reject a regularization detail. Access: MANAGER, HR_ADMIN")
    @PatchMapping("/{regularizationId}/details/{detailId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RegularizationResponse>> actionDetail(
            @PathVariable Long regularizationId,
            @PathVariable Long detailId,
            @Valid @RequestBody RegularizationDetailActionRequest request
    ) {
        RegularizationResponse response;

        if (request.getStatus() == com.my_hourly.attendance.entity.RegularizationDetailStatus.APPROVED) {
            response = regularizationService.approveDetail(
                    regularizationId, detailId, request
            );
        } else if (request.getStatus() == com.my_hourly.attendance.entity.RegularizationDetailStatus.REJECTED) {
            response = regularizationService.rejectDetail(
                    regularizationId, detailId, request
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<RegularizationResponse>builder()
                            .success(false)
                            .message("Invalid action. Only APPROVED or REJECTED is allowed.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }

        return ResponseEntity.ok(
                ApiResponse.<RegularizationResponse>builder()
                        .success(true)
                        .message("Detail action processed successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Revert an approved regularization detail. Access: HR_ADMIN, SUPER_ADMIN")
    @PostMapping("/{regularizationId}/details/{detailId}/revert")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> revertDetail(
            @PathVariable Long regularizationId,
            @PathVariable Long detailId
    ) {
        regularizationService.revertDetail(regularizationId, detailId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Regularization detail reverted successfully.")
                        .data("Reverted to original attendance state.")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
