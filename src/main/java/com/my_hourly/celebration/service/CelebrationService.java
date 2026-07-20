package com.my_hourly.celebration.service;

import java.util.List;

import com.my_hourly.celebration.dto.*;
import com.my_hourly.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CelebrationService {

    CelebrationResponse createPost(CreateCelebrationRequest request);
    List<CelebrationResponse> getAllPosts();
    List<CelebrationResponse> getByType(String type);
    CelebrationResponse getPost(Long id);
    ResponseEntity<ApiResponse> deletePost(Long id);
    ResponseEntity<ApiResponse> likePost(Long postId, Long employeeId);
    ResponseEntity<ApiResponse>unlikePost(Long postId, Long employeeId);
    ResponseEntity<ApiResponse> addComment(Long postId, CommentRequest request);
    ResponseEntity<ApiResponse> deleteComment(Long postId);

}