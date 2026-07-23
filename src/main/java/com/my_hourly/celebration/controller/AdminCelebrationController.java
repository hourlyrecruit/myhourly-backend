package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.CelebrationResponse;
import com.my_hourly.celebration.dto.CreateCelebrationRequest;
import com.my_hourly.celebration.service.CelebrationService;
import com.my_hourly.celebration.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/celebrations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_ADMIN')")
public class AdminCelebrationController {

    private final CelebrationService celebrationService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCelebration(@RequestBody CreateCelebrationRequest request) {
        return celebrationService.createPost(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CelebrationResponse> getCelebration(@PathVariable Long id) {
        return ResponseEntity.ok(celebrationService.getPost(id));
    }

    @GetMapping
    public ResponseEntity<List<CelebrationResponse>> getAllCelebrations() {
        return ResponseEntity.ok(celebrationService.getAllPosts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long id, @RequestBody CreateCelebrationRequest request) {
        return celebrationService.updatePost(id, request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePostPatch(@PathVariable Long id, @RequestBody CreateCelebrationRequest request) {
        return celebrationService.updatePostPatch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCelebration(@PathVariable Long id) {
        return celebrationService.deletePost(id);
    }
}