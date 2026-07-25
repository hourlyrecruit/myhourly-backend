package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.CelebrationResponse;
import com.my_hourly.celebration.dto.CreateCelebrationRequest;
import com.my_hourly.celebration.service.CelebrationService;
import com.my_hourly.celebration.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/celebrations")
@RequiredArgsConstructor
@Tag(name="14-Admin Celebration Management")
public class AdminCelebrationController {

    private final CelebrationService celebrationService;

    @PostMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Create a new celebration (HR Admin only)")
    public ResponseEntity<ApiResponse> createCelebration(@RequestBody CreateCelebrationRequest request) {
        return celebrationService.createPost(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Get celebration by ID (HR Admin only)")
    public ResponseEntity<CelebrationResponse> getCelebration(@PathVariable Long id) {
        return ResponseEntity.ok(celebrationService.getPost(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Get all celebrations (HR Admin only)")
    public ResponseEntity<List<CelebrationResponse>> getAllCelebrations() {
        return ResponseEntity.ok(celebrationService.getAllPosts());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Update celebration by ID (HR Admin only)")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long id, @RequestBody CreateCelebrationRequest request) {
        return celebrationService.updatePost(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update celebration by ID (HR Admin only)")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public ResponseEntity<ApiResponse> updatePostPatch(@PathVariable Long id, @RequestBody CreateCelebrationRequest request) {
        return celebrationService.updatePostPatch(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Delete celebration by ID (HR Admin only)")
    public ResponseEntity<ApiResponse> deleteCelebration(@PathVariable Long id) {
        return celebrationService.deletePost(id);
    }
}