package com.my_hourly.settings.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.settings.leave.dto.request.LeaveSettingsRequest;
import com.my_hourly.settings.leave.dto.response.LeaveSettingsResponse;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Settings - Leave", description = "Manage leave settings")

@RestController
@RequestMapping("/api/v1/settings/leave")
@RequiredArgsConstructor
public class LeaveSettingsController {

    private final LeaveSettingsService leaveSettingsService;

    @Operation(summary = "Get Leave Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveSettingsResponse>> getLeaveSettings() {

        LeaveSettingsResponse response = leaveSettingsService.getLeaveSettings();

        ApiResponse<LeaveSettingsResponse> apiResponse =
                ApiResponse.<LeaveSettingsResponse>builder()
                        .success(true)
                        .message("Leave settings fetched successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update Leave Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveSettingsResponse>> updateLeaveSettings(
            @Valid @RequestBody LeaveSettingsRequest request) {

        LeaveSettingsResponse response =
                leaveSettingsService.updateLeaveSettings(request);

        ApiResponse<LeaveSettingsResponse> apiResponse =
                ApiResponse.<LeaveSettingsResponse>builder()
                        .success(true)
                        .message("Leave settings updated successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }
}