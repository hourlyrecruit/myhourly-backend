package com.my_hourly.settings.company.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.settings.company.dto.request.CompanySettingsRequest;
import com.my_hourly.settings.company.dto.response.CompanySettingsResponse;
import com.my_hourly.settings.company.service.CompanySettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Settings - Company", description = "Manage company settings")

@RestController
@RequestMapping("/api/v1/settings/company")
@RequiredArgsConstructor
public class CompanySettingsController {

    private final CompanySettingsService service;

    @Operation(summary = "Get Company Settings. Access: SUPER_ADMIN, HR_ADMIN, MANAGER")
    @GetMapping
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
    @PutMapping
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

}