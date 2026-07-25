package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.CelebrationResponse;
import com.my_hourly.celebration.dto.CommentRequest;
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
@RequestMapping("/api/employee/celebrations")
@RequiredArgsConstructor
@Tag(name = "16-Employee Celebration", description = "Employee Celebration API")
public class EmployeeCelebrationController {

    private final CelebrationService celebrationService;


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all celebrations for employee. Access: Any logged In user")
    public ResponseEntity<List<CelebrationResponse>> getAllPosts() {
        return ResponseEntity.ok(celebrationService.getAllPosts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get celebration by ID. Access: Any logged In user")
    public ResponseEntity<CelebrationResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(celebrationService.getPost(id));
    }

    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Filter celebrations by type. Access: Any logged In user")
    public ResponseEntity<List<CelebrationResponse>> filterByType(@RequestParam String type) {
        return ResponseEntity.ok(celebrationService.getByType(type));
    }

    @PostMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Like a celebration. Access: Any logged In user")
    public ResponseEntity<ApiResponse> likePost(@PathVariable Long postId) {
        return celebrationService.likePost(postId);
    }

    @DeleteMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> unlikePost(@PathVariable Long postId) {
        return celebrationService.unlikePost(postId);
    }

    @PostMapping("/{postId}/comment")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add comment to a celebration. Access: Any logged In user")
    public ResponseEntity<ApiResponse> addComment(@PathVariable Long postId, @RequestBody CommentRequest request) {
        return celebrationService.addComment(postId, request);
    }


    @DeleteMapping("/comment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete comment from a celebration. Access: Any logged In user")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long commentId) {
        return celebrationService.deleteComment(commentId);
    }
}