package com.my_hourly.celebration.service;

import java.util.List;

import com.my_hourly.celebration.dto.*;
import com.my_hourly.celebration.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CelebrationService {

    ResponseEntity<ApiResponse> createPost(CreateCelebrationRequest request);
    List<CelebrationResponse> getAllPosts();
    List<CelebrationResponse> getByType(String type);
    CelebrationResponse getPost(Long id);
    ResponseEntity<ApiResponse> updatePost(Long id, CreateCelebrationRequest request);
    ResponseEntity<ApiResponse> updatePostPatch(Long id, CreateCelebrationRequest request);
    ResponseEntity<ApiResponse> deletePost(Long id);
    ResponseEntity<ApiResponse> likePost(Long postId);
    ResponseEntity<ApiResponse> unlikePost(Long postId);
    ResponseEntity<ApiResponse> addComment(Long postId, CommentRequest request);
    ResponseEntity<ApiResponse> deleteComment(Long postId);

}