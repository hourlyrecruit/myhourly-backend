package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.AnnouncementRequest;
import com.my_hourly.celebration.dto.AnnouncementResponse;
import com.my_hourly.celebration.service.AnnouncementService;
import com.my_hourly.celebration.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "15-AnnouncementController")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PreAuthorize("hasRole('HR_ADMIN')")
    @PostMapping
    @Operation(summary = "Create announcement  Access: (HR Admin only)")
    public ResponseEntity<ApiResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return announcementService.createAnnouncement(request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update announcement by ID  Access: (HR Admin only)")
    public ResponseEntity<ApiResponse> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        return announcementService.updateAnnouncement(id, request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update announcement by ID  Access: (HR Admin only)")
    public ResponseEntity<ApiResponse> updateAnnouncementPatch(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        return announcementService.updateAnnouncementPatch(id, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get announcement by ID Access: (Any Logged In User")
    public ResponseEntity<AnnouncementResponse> getAnnouncement(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getAnnouncement(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Get all announcements Access: (HR Admin only)")
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete announcement by ID  Access: (HR Admin only)")
    public ResponseEntity<ApiResponse> deleteAnnouncement(@PathVariable Long id) {
        return announcementService.deleteAnnouncement(id);
    }
}