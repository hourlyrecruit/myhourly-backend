package com.my_hourly.settings.workLogs.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.settings.workLogs.dto.request.WorkLogSettingsRequest;
import com.my_hourly.settings.workLogs.dto.response.WorkLogSettingsResponse;
import com.my_hourly.settings.workLogs.service.WorkLogSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/settings/work-log")
@RequiredArgsConstructor
public class WorkLogSettingsController {

    private final WorkLogSettingsService workLogSettingsService;

    @GetMapping
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

    @PutMapping
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
