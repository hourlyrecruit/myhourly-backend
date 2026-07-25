package com.my_hourly.settings.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.settings.attendance.dto.request.AttendanceSettingsRequest;
import com.my_hourly.settings.attendance.dto.response.AttendanceSettingsResponse;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import com.my_hourly.settings.company.dto.request.CompanySettingsRequest;
import com.my_hourly.settings.company.dto.response.CompanySettingsResponse;
import com.my_hourly.settings.company.service.CompanySettingsService;
import com.my_hourly.settings.leave.dto.request.LeaveSettingsRequest;
import com.my_hourly.settings.leave.dto.response.LeaveSettingsResponse;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import com.my_hourly.settings.notification.dto.request.NotificationSettingsRequest;
import com.my_hourly.settings.notification.dto.response.NotificationSettingsResponse;
import com.my_hourly.settings.notification.service.NotificationSettingsService;
import com.my_hourly.settings.workLogs.dto.request.WorkLogSettingsRequest;
import com.my_hourly.settings.workLogs.dto.response.WorkLogSettingsResponse;
import com.my_hourly.settings.workLogs.service.WorkLogSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Settings Controller", description = "Manage attendance settings")

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {

    private final AttendanceSettingsService attendanceSettingsService;
    private final LeaveSettingsService leaveSettingsService;
    private final NotificationSettingsService notificationSettingsService;
    private final CompanySettingsService service;
    private final WorkLogSettingsService workLogSettingsService;

    @Operation(summary = "Get Attendance Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping("/attendance")
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
    @PutMapping("/attendance")
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

//    ===================================================================

    @Operation(summary = "Get Leave Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping("/leave")
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
    @PutMapping("/leave")
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

//    =================================================



    @Operation(summary = "Get Notification Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping("/notification")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<NotificationSettingsResponse>> getNotificationSettings() {

        NotificationSettingsResponse response =
                notificationSettingsService.getNotificationSettings();

        ApiResponse<NotificationSettingsResponse> apiResponse =
                ApiResponse.<NotificationSettingsResponse>builder()
                        .success(true)
                        .message("Notification settings fetched successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update Notification Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PutMapping("/notification")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<NotificationSettingsResponse>> updateNotificationSettings(
            @Valid @RequestBody NotificationSettingsRequest request) {

        NotificationSettingsResponse response =
                notificationSettingsService.updateNotificationSettings(request);

        ApiResponse<NotificationSettingsResponse> apiResponse =
                ApiResponse.<NotificationSettingsResponse>builder()
                        .success(true)
                        .message("Notification settings updated successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }
//==================================================================


    @Operation(summary = "Get Company Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping("/company")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CompanySettingsResponse>> getCompanySettings() {

        CompanySettingsResponse response = service.getCompanySettings();

        return ResponseEntity.ok(
                ApiResponse.<CompanySettingsResponse>builder()
                        .success(true)
                        .message("Company settings fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Operation(summary = "Update Company Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PutMapping("/company")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CompanySettingsResponse>> updateCompanySettings(
            @Valid @RequestBody CompanySettingsRequest request) {

        CompanySettingsResponse response = service.updateCompanySettings(request);

        return ResponseEntity.ok(
                ApiResponse.<CompanySettingsResponse>builder()
                        .success(true)
                        .message("Company settings updated successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
//============================================================
    @Operation(summary = "Get Work Log Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping("/work-log")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<WorkLogSettingsResponse>> getWorkLogSettings() {

        WorkLogSettingsResponse response =
                workLogSettingsService.getWorkLogSettings();

        ApiResponse<WorkLogSettingsResponse> apiResponse =
                ApiResponse.<WorkLogSettingsResponse>builder()
                        .success(true)
                        .message("Work log settings fetched successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update Work Log Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @PutMapping("/work-log")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<WorkLogSettingsResponse>> updateWorkLogSettings(
            @Valid @RequestBody WorkLogSettingsRequest request) {

        WorkLogSettingsResponse response =
                workLogSettingsService.updateWorkLogSettings(request);

        ApiResponse<WorkLogSettingsResponse> apiResponse =
                ApiResponse.<WorkLogSettingsResponse>builder()
                        .success(true)
                        .message("Work log settings updated successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }
}
