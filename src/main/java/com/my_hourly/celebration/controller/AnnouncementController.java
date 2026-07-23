package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.AnnouncementRequest;
import com.my_hourly.celebration.dto.AnnouncementResponse;
import com.my_hourly.celebration.service.AnnouncementService;
import com.my_hourly.celebration.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return announcementService.createAnnouncement(request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        return announcementService.updateAnnouncement(id, request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAnnouncementPatch(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        return announcementService.updateAnnouncementPatch(id, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getAnnouncement(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getAnnouncement(id));
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAnnouncement(@PathVariable Long id) {
        return announcementService.deleteAnnouncement(id);
    }
}