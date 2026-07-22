package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.request.LeaveTypeRequest;
import com.my_hourly.leave.api.response.LeaveTypeResponse;
import com.my_hourly.leave.service.LeaveTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-types")
@RequiredArgsConstructor
@Tag(name="05-Leave Type Controller (Leave Policy)", description = "Here needs to define what types of leave like: Sick leave = 12, Causal Leave=12 -> total 24/year")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @Operation(summary = "Create Leave Type. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> createLeaveType(
            @Valid @RequestBody LeaveTypeRequest request) {

        LeaveTypeResponse response = leaveTypeService.createLeaveType(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LeaveTypeResponse>builder()
                        .success(true)
                        .message("Leave type created successfully.")
                        .data(response)
                        .build());
    }

    @Operation(summary = "Update Leave Type. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PutMapping("/{leaveTypeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> updateLeaveType(
            @PathVariable Long leaveTypeId,
            @Valid @RequestBody LeaveTypeRequest request) {

        LeaveTypeResponse response =
                leaveTypeService.updateLeaveType(leaveTypeId, request);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponse>builder()
                        .success(true)
                        .message("Leave type updated successfully.")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get Leave Type by ID. Access: Authenticated Users")
    @GetMapping("/{leaveTypeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> getLeaveType(
            @PathVariable Long leaveTypeId) {

        LeaveTypeResponse response =
                leaveTypeService.getLeaveType(leaveTypeId);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponse>builder()
                        .success(true)
                        .message("Leave type fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Get All Leave Types. Access: Authenticated Users")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> getAllLeaveTypes() {

        List<LeaveTypeResponse> response =
                leaveTypeService.getAllLeaveTypes();

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveTypeResponse>>builder()
                        .success(true)
                        .message("Leave types fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get Active Leave Types. Access: Authenticated Users")
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> getActiveLeaveTypes() {

        List<LeaveTypeResponse> response =
                leaveTypeService.getActiveLeaveTypes();

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveTypeResponse>>builder()
                        .success(true)
                        .message("Active leave types fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Activate Leave Type. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PatchMapping("/{leaveTypeId}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> activateLeaveType(
            @PathVariable Long leaveTypeId) {

        LeaveTypeResponse response =
                leaveTypeService.activateLeaveType(leaveTypeId);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponse>builder()
                        .success(true)
                        .message("Leave type activated successfully.")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Deactivate Leave Type. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PatchMapping("/{leaveTypeId}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> deactivateLeaveType(
            @PathVariable Long leaveTypeId) {

        LeaveTypeResponse response =
                leaveTypeService.deactivateLeaveType(leaveTypeId);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponse>builder()
                        .success(true)
                        .message("Leave type deactivated successfully.")
                        .data(response)
                        .build()
        );
    }

}