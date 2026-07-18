package com.my_hourly.settings.attendance.controller;

import com.my_hourly.common.response.ApiResponse;
import com.my_hourly.settings.attendance.dto.request.AttendanceSettingsRequest;
import com.my_hourly.settings.attendance.dto.response.AttendanceSettingsResponse;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/settings/attendance")
@RequiredArgsConstructor
public class AttendanceSettingsController {

    private final AttendanceSettingsService attendanceSettingsService;

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
