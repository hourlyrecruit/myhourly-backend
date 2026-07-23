package com.my_hourly.settings.attendance.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.settings.attendance.dto.request.AttendanceSettingsRequest;
import com.my_hourly.settings.attendance.dto.response.AttendanceSettingsResponse;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Settings - Attendance", description = "Manage attendance settings")

@RestController
@RequestMapping("/api/v1/settings/attendance")
@RequiredArgsConstructor
public class AttendanceSettingsController {

    private final AttendanceSettingsService attendanceSettingsService;

    @Operation(summary = "Get Attendance Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceSettingsResponse>> getAttendanceSettings() {

        AttendanceSettingsResponse response = attendanceSettingsService.getAttendanceSettings();

        ApiResponse<AttendanceSettingsResponse> apiResponse = ApiResponse.<AttendanceSettingsResponse>builder()
                .success(true)
                .message("Attendance settings fetched successfully.")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update Attendance Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceSettingsResponse>> updateAttendanceSettings(
            @Valid @RequestBody AttendanceSettingsRequest request) {

        AttendanceSettingsResponse response =
                attendanceSettingsService.updateAttendanceSettings(request);

        ApiResponse<AttendanceSettingsResponse> apiResponse = ApiResponse.<AttendanceSettingsResponse>builder()
                .success(true)
                .message("Attendance settings updated successfully.")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
