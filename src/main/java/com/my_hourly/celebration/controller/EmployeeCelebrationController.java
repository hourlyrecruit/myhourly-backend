package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.CelebrationResponse;
import com.my_hourly.celebration.dto.CommentRequest;
import com.my_hourly.celebration.service.CelebrationService;
import com.my_hourly.celebration.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/celebrations")
@RequiredArgsConstructor
public class EmployeeCelebrationController {

    private final CelebrationService celebrationService;


    @GetMapping
    public ResponseEntity<List<CelebrationResponse>> getAllPosts() {
        return ResponseEntity.ok(celebrationService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CelebrationResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(celebrationService.getPost(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CelebrationResponse>> filterByType(@RequestParam String type) {
        return ResponseEntity.ok(celebrationService.getByType(type));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> likePost(@PathVariable Long postId) {
        return celebrationService.likePost(postId);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> unlikePost(@PathVariable Long postId) {
        return celebrationService.unlikePost(postId);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<ApiResponse> addComment(@PathVariable Long postId, @RequestBody CommentRequest request) {
        return celebrationService.addComment(postId, request);
    }


    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long commentId) {
        return celebrationService.deleteComment(commentId);
    }
}