package com.my_hourly.celebration.service;

import com.my_hourly.celebration.dto.AnnouncementRequest;
import com.my_hourly.celebration.dto.AnnouncementResponse;
import com.my_hourly.celebration.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AnnouncementService {

    ResponseEntity<ApiResponse> createAnnouncement(AnnouncementRequest request);
    ResponseEntity<ApiResponse> updateAnnouncement(Long id, AnnouncementRequest request);
    ResponseEntity<ApiResponse> updateAnnouncementPatch(Long id, AnnouncementRequest request);
    AnnouncementResponse getAnnouncement(Long id);
    List<AnnouncementResponse> getAllAnnouncements();
    ResponseEntity<ApiResponse> deleteAnnouncement(Long id);
}