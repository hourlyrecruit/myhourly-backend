package com.my_hourly.celebration.serviceImpl;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.celebration.dto.AnnouncementRequest;
import com.my_hourly.celebration.dto.AnnouncementResponse;
import com.my_hourly.celebration.entity.Announcement;
import com.my_hourly.celebration.repository.AnnouncementRepository;
import com.my_hourly.celebration.service.AnnouncementService;
import com.my_hourly.celebration.dto.ApiResponse;
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

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("User not authenticated");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }

    private void validateHr() {
        User user = getLoggedInUser();
        if (user.getRole() != RoleName.HR_ADMIN) {
            throw new IllegalStateException("Only HR can manage announcements");
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createAnnouncement(AnnouncementRequest request) {
        validateHr();
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setActive(true);
        Announcement saved = announcementRepository.save(announcement);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                true,
                                "Announcement created successfully",
                                mapToResponse(saved)));
    }

    @Override
    public ResponseEntity<ApiResponse> updateAnnouncement(Long id, AnnouncementRequest request) {
        validateHr();
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));
        announcement.setTitle(request.getTitle());announcement.setMessage(request.getMessage());

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Announcement updated successfully",
                        mapToResponse(updatedAnnouncement)));
    }

    @Override
    public ResponseEntity<ApiResponse> updateAnnouncementPatch(Long id, AnnouncementRequest request) {
        validateHr();
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));
        if (request.getTitle() != null) {
            announcement.setTitle(request.getTitle());
        }
        if (request.getMessage() != null) {
            announcement.setMessage(request.getMessage());
        }
        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Announcement updated successfully",
                        mapToResponse(updatedAnnouncement)));
    }
    @Override
    public AnnouncementResponse getAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));
        return mapToResponse(announcement);
    }

    @Override
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository
                .findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ResponseEntity<ApiResponse> deleteAnnouncement(Long id) {
        validateHr();
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));
        AnnouncementResponse response = mapToResponse(announcement);
        announcementRepository.delete(announcement);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Announcement deleted successfully",
                        response));
    }

    private AnnouncementResponse mapToResponse(Announcement announcement) {

        AnnouncementResponse response = new AnnouncementResponse();

        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setMessage(announcement.getMessage());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setActive(announcement.isActive());

        return response;
    }
}