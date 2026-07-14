package com.my_hourly.master.api.controller;

import com.my_hourly.common.response.ApiResponse;
import com.my_hourly.common.response.PageResponse;
import com.my_hourly.master.api.request.CreateJobTitleRequest;
import com.my_hourly.master.api.request.UpdateJobTitleRequest;
import com.my_hourly.master.api.response.JobTitleResponse;
import com.my_hourly.master.service.JobTitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-titles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN, MANAGER, HR_ADMIN')")
@Tag(name = "JobTitle Controller", description = "Access: SUPER_ADMIN, MANAGER, HR_ADMIN")
public class JobTitleController {

    private final JobTitleService jobTitleService;

    @PostMapping
    @PreAuthorize("hasAuthority('job-title:create')")
    @Operation(description = "Add JobTitle like: Backend Engineer, Frontend Engineer, Full Stack Engineer, Software Developer, DevOps Engineer etc.")
    public ResponseEntity<ApiResponse<JobTitleResponse>> create(
            @Valid @RequestBody CreateJobTitleRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<JobTitleResponse>builder()
                        .success(true)
                        .message("Job title created successfully.")
                        .data(jobTitleService.create(request))
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('job-title:view')")
    public ResponseEntity<ApiResponse<JobTitleResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<JobTitleResponse>builder()
                        .success(true)
                        .message("Job title fetched successfully.")
                        .data(jobTitleService.getById(id))
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('job-title:view')")
    public ResponseEntity<ApiResponse<PageResponse<JobTitleResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "jobTitle") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<JobTitleResponse>>builder()
                        .success(true)
                        .message("Job titles fetched successfully.")
                        .data(jobTitleService.getAll(
                                page,
                                size,
                                search,
                                sortBy,
                                sortDirection
                        ))
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('job-title:update')")
    public ResponseEntity<ApiResponse<JobTitleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobTitleRequest request) {

        return ResponseEntity.ok(
                ApiResponse.<JobTitleResponse>builder()
                        .success(true)
                        .message("Job title updated successfully.")
                        .data(jobTitleService.update(id, request))
                        .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('job-title:update')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        jobTitleService.changeStatus(id, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Job title status updated successfully.")
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('job-title:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        jobTitleService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Job title deleted successfully.")
                        .build());
    }
}