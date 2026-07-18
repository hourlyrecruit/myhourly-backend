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

@RestController
@RequestMapping("/api/v1/settings/leave")
@RequiredArgsConstructor
public class LeaveSettingsController {

    private final LeaveSettingsService leaveSettingsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
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

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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