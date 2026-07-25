package com.my_hourly.celebration.serviceImpl;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.celebration.dto.*;
import com.my_hourly.celebration.entity.*;
import com.my_hourly.celebration.repository.CelebrationCommentRepository;
import com.my_hourly.celebration.repository.CelebrationLikeRepository;
import com.my_hourly.celebration.repository.CelebrationRepository;
import com.my_hourly.celebration.repository.CelebrationTagRepository;
import com.my_hourly.celebration.service.CelebrationService;
import com.my_hourly.celebration.dto.ApiResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.security.user.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class CelebrationServiceImpl implements CelebrationService {

    private final CelebrationRepository celebrationRepository;

    private final CelebrationTagRepository tagRepository;

    private final CelebrationLikeRepository likeRepository;

    private final CelebrationCommentRepository commentRepository;

    private final EmployeeRepository employeeRepository;

    private final UserRepository userRepository;

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("User not authenticated");
        }

        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }


    @Override
    public ResponseEntity<ApiResponse> createPost(CreateCelebrationRequest request) {
        User user = getLoggedInUser();
        if (user.getRole() != RoleName.HR_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(
                            false,
                            "Only HR can create celebration posts",
                            null));
        }
        CelebrationPost post = new CelebrationPost();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setImageUrl(request.getImageUrl());
        post.setCelebrationType(request.getCelebrationType());
        post.setCreatedAt(LocalDateTime.now());
        post.setCreatedBy(user.getId());
        post.setActive(true);
        CelebrationPost savedPost = celebrationRepository.save(post);
        if (request.getTaggedEmployeeIds() != null) {
            List<CelebrationTag> tags = request.getTaggedEmployeeIds()
                    .stream()
                    .map(empId -> {
                        CelebrationTag tag = new CelebrationTag();
                        tag.setCelebrationPost(savedPost);
                        tag.setEmployeeId(empId);
                        return tag;
                    })
                    .toList();

            tagRepository.saveAll(tags);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(
                        true,
                        "Celebration post created successfully",
                        mapToResponse(savedPost)));
    }

    @Override
    public List<CelebrationResponse> getAllPosts() {
        return celebrationRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
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
    @Override
    public ResponseEntity<ApiResponse> updatePost(Long id, CreateCelebrationRequest request) {
        User user = getLoggedInUser();
        if (user.getRole() != RoleName.HR_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(
                            false,
                            "Only HR can update celebration posts",
                            null));
        }

        CelebrationPost post = celebrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Celebration post not found"));

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setImageUrl(request.getImageUrl());
        post.setCelebrationType(request.getCelebrationType());

        CelebrationPost savedPost = celebrationRepository.save(post);

        // Replace tagged employees
        tagRepository.deleteByCelebrationPostId(savedPost.getId());

        if (request.getTaggedEmployeeIds() != null &&
                !request.getTaggedEmployeeIds().isEmpty()) {

            List<CelebrationTag> tags = request.getTaggedEmployeeIds()
                    .stream()
                    .map(empId -> {
                        CelebrationTag tag = new CelebrationTag();
                        tag.setCelebrationPost(savedPost);
                        tag.setEmployeeId(empId);
                        return tag;
                    })
                    .toList();

            tagRepository.saveAll(tags);
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Celebration post updated successfully",
                        mapToResponse(savedPost)));
    }
    @Override
    public ResponseEntity<ApiResponse> updatePostPatch(Long id, CreateCelebrationRequest request) {
        User user = getLoggedInUser();
        if (user.getRole() != RoleName.HR_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(
                            false,
                            "Only HR can update celebration posts",
                            null));
        }
        CelebrationPost post = celebrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Celebration post not found"));
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }
        if (request.getCelebrationType() != null) {
            post.setCelebrationType(request.getCelebrationType());
        }
        CelebrationPost savedPost = celebrationRepository.save(post);
        if (request.getTaggedEmployeeIds() != null) {
            tagRepository.deleteByCelebrationPostId(savedPost.getId());
            List<CelebrationTag> tags = request.getTaggedEmployeeIds()
                    .stream()
                    .map(empId -> {
                        CelebrationTag tag = new CelebrationTag();
                        tag.setCelebrationPost(savedPost);
                        tag.setEmployeeId(empId);
                        return tag;
                    })
                    .toList();
            tagRepository.saveAll(tags);
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Celebration post updated successfully",
                        mapToResponse(savedPost)
                )
        );
    }


    private CelebrationResponse mapToResponse(CelebrationPost post) {

        CelebrationResponse response = new CelebrationResponse();

        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setImageUrl(post.getImageUrl());
        response.setCelebrationType(post.getCelebrationType());
        response.setCreatedAt(post.getCreatedAt());

        // Created By
        response.setCreatedByUserId(post.getCreatedBy());

        userRepository.findById(post.getCreatedBy())
                .ifPresent(user -> response.setCreatedByName(user.getUsername()));

        // =========================
        // Tagged Employees
        // =========================

        List<Long> employeeIds = tagRepository.findByCelebrationPostId(post.getId())
                .stream()
                .map(CelebrationTag::getEmployeeId)
                .toList();

        Map<Long, Employee> taggedEmployeeMap = employeeRepository.findByIdIn(employeeIds)
                .stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));

        List<TaggedEmployeeResponse> taggedEmployees = employeeIds.stream()
                .map(employeeId -> {
                    TaggedEmployeeResponse dto = new TaggedEmployeeResponse();

                    Employee employee = taggedEmployeeMap.get(employeeId);

                    if (employee != null) {
                        dto.setEmployeeId(employee.getId());

                        dto.setEmployeeName(
                                employee.getFirstName() +
                                        (employee.getLastName() != null
                                                ? " " + employee.getLastName()
                                                : "")
                        );

                        dto.setProfilePhoto(employee.getProfilePhoto());
                    }

                    return dto;
                })
                .toList();

        response.setTaggedEmployees(taggedEmployees);

        // =========================
        // Likes & Comments
        // =========================

        List<CelebrationLike> likeEntities =
                likeRepository.findByCelebrationPostId(post.getId());

        List<CelebrationComment> commentEntities =
                commentRepository.findByCelebrationPostId(post.getId());

        List<Long> userIds = java.util.stream.Stream.concat(
                        likeEntities.stream().map(CelebrationLike::getUserId),
                        commentEntities.stream().map(CelebrationComment::getUserId))
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findByIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<Long, Employee> userEmployeeMap = employeeRepository.findByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(
                        employee -> employee.getUser().getId(),
                        employee -> employee
                ));

        List<LikeResponse> likes = likeEntities.stream()
                .map(like -> {
                    LikeResponse dto = new LikeResponse();

                    dto.setUserId(like.getUserId());

                    User user = userMap.get(like.getUserId());

                    if (user != null) {
                        dto.setUsername(user.getUsername());

                        Employee employee = userEmployeeMap.get(user.getId());

                        if (employee != null) {
                            dto.setProfilePhoto(employee.getProfilePhoto());
                        }
                    }

                    return dto;
                })
                .toList();

        response.setLikes(likes);

        List<CommentResponse> comments = commentEntities.stream()
                .map(comment -> {
                    CommentResponse dto = new CommentResponse();

                    dto.setId(comment.getId());
                    dto.setUserId(comment.getUserId());
                    dto.setComment(comment.getComment());
                    dto.setCommentedAt(comment.getCommentedAt());

                    User user = userMap.get(comment.getUserId());

                    if (user != null) {
                        dto.setUsername(user.getUsername());

                        Employee employee = userEmployeeMap.get(user.getId());

                        if (employee != null) {
                            dto.setProfilePhoto(employee.getProfilePhoto());
                        }
                    }

                    return dto;
                })
                .toList();

        response.setLikes(likes);
        response.setComments(comments);

        response.setTotalLikes(likes.size());
        response.setTotalComments(comments.size());

        return response;
    }

    @Override
    public ResponseEntity<ApiResponse> deletePost(Long id) {

        User user = getLoggedInUser();

        if (user.getRole() != RoleName.HR_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(
                            false,
                            "Only HR can delete celebration posts",
                            null));
        }

        CelebrationPost post = celebrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Celebration post not found"));

        CelebrationResponse response = mapToResponse(post);

        celebrationRepository.delete(post);

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Celebration post deleted successfully",
                        response
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponse> likePost(Long postId) {

        User user = getLoggedInUser();

        CelebrationPost post = celebrationRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Celebration post not found"));

        if (likeRepository.existsByCelebrationPostIdAndUserId(postId, user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(
                            false,
                            "You have already liked this post",
                            null));
        }
        CelebrationLike like = new CelebrationLike();
        like.setCelebrationPost(post);
        like.setUserId(user.getId());
        like.setLikedAt(LocalDateTime.now());
        likeRepository.save(like);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Post liked successfully",
                        mapToResponse(post)));
    }

    @Override
    public ResponseEntity<ApiResponse> unlikePost(Long postId) {

        User user = getLoggedInUser();

        CelebrationPost post = celebrationRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Celebration post not found"));

        if (!likeRepository.existsByCelebrationPostIdAndUserId(postId, user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(
                            false,
                            "Like not found",
                            null));
        }

        likeRepository.deleteByCelebrationPostIdAndUserId(postId, user.getId());
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Like removed successfully",
                        mapToResponse(post)));
    }

    @Override
    public ResponseEntity<ApiResponse> addComment(Long postId, CommentRequest request) {

        User user = getLoggedInUser();

        CelebrationPost post = celebrationRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Celebration post not found"));

        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(
                            false,
                            "Comment cannot be empty",
                            null));
        }
        CelebrationComment comment = new CelebrationComment();
        comment.setCelebrationPost(post);
        comment.setUserId(user.getId());
        comment.setComment(request.getComment().trim());
        comment.setCommentedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Comment added successfully",
                        mapToResponse(post)));
    }
    @Override
    public ResponseEntity<ApiResponse> deleteComment(Long commentId) {

        User loggedInUser = getLoggedInUser();

        CelebrationComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (loggedInUser.getRole() != RoleName.HR_ADMIN &&
                !comment.getUserId().equals(loggedInUser.getId())) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(
                            false,
                            "You can delete only your own comment",
                            null));
        }
        CelebrationPost post = comment.getCelebrationPost();
        commentRepository.delete(comment);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Comment deleted successfully",
                        mapToResponse(post)));
    }
}
