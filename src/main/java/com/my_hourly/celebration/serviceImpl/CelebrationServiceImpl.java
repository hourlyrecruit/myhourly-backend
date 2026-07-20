package com.my_hourly.celebration.serviceImpl;

import com.my_hourly.celebration.dto.CelebrationResponse;
import com.my_hourly.celebration.dto.CommentRequest;
import com.my_hourly.celebration.dto.CreateCelebrationRequest;
import com.my_hourly.celebration.entity.*;
import com.my_hourly.celebration.repository.CelebrationCommentRepository;
import com.my_hourly.celebration.repository.CelebrationLikeRepository;
import com.my_hourly.celebration.repository.CelebrationRepository;
import com.my_hourly.celebration.repository.CelebrationTagRepository;
import com.my_hourly.celebration.service.CelebrationService;
import com.my_hourly.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CelebrationServiceImpl implements CelebrationService {
    @Autowired
    private CelebrationRepository celebrationRepository;
    @Autowired
    private CelebrationTagRepository tagRepository;
    @Autowired
    private CelebrationLikeRepository likeRepository;
    @Autowired
    private CelebrationCommentRepository commentRepository;


    @Override
    public CelebrationResponse createPost(CreateCelebrationRequest request) {
        CelebrationPost post = new CelebrationPost();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setImageUrl(request.getImageUrl());
        post.setCelebrationType(request.getCelebrationType());
        post.setCreatedAt(LocalDateTime.now());
        post.setCreatedBy(1L);      // Replace with logged-in admin later
        post.setActive(true);
        CelebrationPost savedPost = celebrationRepository.save(post);
        if (request.getTaggedEmployeeIds() != null) {
            for (Long empId : request.getTaggedEmployeeIds()) {
                CelebrationTag tag = new CelebrationTag();
                tag.setCelebrationPost(savedPost);
                tag.setEmployeeId(empId);
                tagRepository.save(tag);
            }
        }
        return mapToResponse(savedPost);
    }


    @Override
    public List<CelebrationResponse> getAllPosts() {
        return celebrationRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CelebrationPost::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CelebrationResponse> getByType(String type) {
        CelebrationType celebrationType = CelebrationType.valueOf(type.toUpperCase());
        return celebrationRepository
                .findByCelebrationType(celebrationType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CelebrationResponse getPost(Long id) {
        CelebrationPost post = celebrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Celebration not found"));
        return mapToResponse(post);
    }
    private CelebrationResponse mapToResponse(CelebrationPost post) {
        CelebrationResponse response = new CelebrationResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setImageUrl(post.getImageUrl());
        response.setCelebrationType(post.getCelebrationType());
        response.setCreatedAt(post.getCreatedAt());
        response.setCreatedBy(post.getCreatedBy());

        List<Long> taggedEmployees = tagRepository
                .findByCelebrationPostId(post.getId())
                .stream()
                .map(CelebrationTag::getEmployeeId)
                .collect(Collectors.toList());

        response.setTaggedEmployees(taggedEmployees);
        response.setTotalLikes(likeRepository.countByCelebrationPostId(post.getId()));
        response.setTotalComments(commentRepository.countByCelebrationPostId(post.getId()));

        return response;
    }

    @Override
    public ResponseEntity<ApiResponse> deletePost(Long id) {
        CelebrationPost post = celebrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Celebration post not found"));
        celebrationRepository.delete(post);
        return ResponseEntity.ok(new ApiResponse(true, "Celebration post deleted successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse> likePost(Long postId, Long employeeId) {
        CelebrationPost post = celebrationRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Celebration post not found"));
        if (likeRepository.existsByCelebrationPostIdAndEmployeeId(postId, employeeId)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "You have already liked this post"));
        }

        CelebrationLike like = new CelebrationLike();
        like.setCelebrationPost(post);
        like.setEmployeeId(employeeId);
        like.setLikedAt(LocalDateTime.now());
        likeRepository.save(like);
        return ResponseEntity.ok(new ApiResponse(true, "Post liked successfully")
        );
    }

    @Override
    public ResponseEntity<ApiResponse> unlikePost(Long postId, Long employeeId) {
        if (!likeRepository.existsByCelebrationPostIdAndEmployeeId(postId, employeeId)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Like not found"));
        }
        likeRepository.deleteByCelebrationPostIdAndEmployeeId(postId, employeeId);
        return ResponseEntity.ok(new ApiResponse(true, "Like removed successfully"));
    }

    @Override
    public ResponseEntity<ApiResponse> addComment(Long postId, CommentRequest request) {
        CelebrationPost post = celebrationRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Celebration post not found"));
        CelebrationComment comment = new CelebrationComment();
        comment.setCelebrationPost(post);
        comment.setEmployeeId(request.getEmployeeId());
        comment.setComment(request.getComment());
        comment.setCommentedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return ResponseEntity.ok(new ApiResponse(true, "Comment added successfully"));
    }

    @Override
    public ResponseEntity<ApiResponse> deleteComment(Long commentId) {
        CelebrationComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
        return ResponseEntity.ok(new ApiResponse(true, "Comment deleted successfully"));
    }
}
