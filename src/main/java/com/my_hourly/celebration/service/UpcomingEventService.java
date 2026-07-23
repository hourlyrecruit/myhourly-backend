package com.my_hourly.celebration.service;

import com.my_hourly.celebration.dto.UpcomingEventRequest;
import com.my_hourly.celebration.dto.UpcomingEventResponse;
import com.my_hourly.celebration.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UpcomingEventService {
    ResponseEntity<ApiResponse> createEvent(UpcomingEventRequest request);
    List<UpcomingEventResponse> getUpcomingEvents();
    ResponseEntity<ApiResponse> deleteEvent(Long id);
    UpcomingEventResponse getEventById(Long id);
    ResponseEntity<ApiResponse> updateEvent(Long id, UpcomingEventRequest request);
    ResponseEntity<ApiResponse> patchEvent(Long id, UpcomingEventRequest request);
}
