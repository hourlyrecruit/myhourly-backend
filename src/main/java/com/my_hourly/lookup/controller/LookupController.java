package com.my_hourly.lookup.controller;

import com.my_hourly.common.response.ApiResponse;
import com.my_hourly.lookup.api.response.LookupResponse;
import com.my_hourly.lookup.api.response.ReportingManagerLookupResponse;
import com.my_hourly.lookup.service.LookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
@Tag(name = "Lookup Controller- Access By Any Logged In User")
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/departments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get List of Departments Id, Name can used for Dropdown in Emp Profile Filling")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getDepartments() {

        return ResponseEntity.ok(
                ApiResponse.<List<LookupResponse>>builder()
                        .success(true)
                        .message("Departments fetched successfully.")
                        .data(lookupService.getDepartments())
                        .build()
        );
    }

    @GetMapping("/departments/{departmentId}/designations")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get List of Designations Id, Name can used for Dropdown in Emp Profile Filling")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getDesignations(
            @RequestParam Long departmentId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LookupResponse>>builder()
                        .success(true)
                        .message("Designations fetched successfully.")
                        .data(lookupService.getDesignations(departmentId))
                        .build()
        );
    }

    @GetMapping("/designations/{designationId}/job-titles")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get List of Job Titles Id, Name can used for Dropdown in Emp Profile Filling")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getJobTitles(
            @RequestParam Long designationId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LookupResponse>>builder()
                        .success(true)
                        .message("Job titles fetched successfully.")
                        .data(lookupService.getJobTitles(designationId))
                        .build()
        );
    }

    @GetMapping("/reporting-managers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get List of Manager's Id, EmployeeCode, Name can used for Dropdown in Emp Profile Filling")
    public ResponseEntity<ApiResponse<List<ReportingManagerLookupResponse>>> getReportingManagers() {

        return ResponseEntity.ok(
                ApiResponse.<List<ReportingManagerLookupResponse>>builder()
                        .success(true)
                        .message("Reporting managers fetched successfully.")
                        .data(lookupService.getReportingManagers())
                        .build()
        );
    }
}
