package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.CelebrationResponse;
import com.my_hourly.celebration.dto.CreateCelebrationRequest;
import com.my_hourly.celebration.service.CelebrationService;
import com.my_hourly.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/celebrations")
@RequiredArgsConstructor
public class AdminCelebrationController {
    @Autowired
    private CelebrationService celebrationService;

    @PostMapping
    public ResponseEntity<CelebrationResponse> createCelebration(@RequestBody CreateCelebrationRequest request) {
        return ResponseEntity.ok(celebrationService.createPost(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CelebrationResponse> getCelebration(@PathVariable Long id) {
        return ResponseEntity.ok(celebrationService.getPost(id));
    }

    @GetMapping
    public ResponseEntity<List<CelebrationResponse>> getAllCelebrations() {
        return ResponseEntity.ok(celebrationService.getAllPosts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCelebration(@PathVariable Long id) {
        return celebrationService.deletePost(id);
    }
}