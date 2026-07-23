package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.UpcomingEventRequest;
import com.my_hourly.celebration.dto.UpcomingEventResponse;
import com.my_hourly.celebration.service.UpcomingEventService;
import com.my_hourly.celebration.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class UpcomingEventController {

    private final UpcomingEventService eventService;
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createEvent(@RequestBody @Valid UpcomingEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<UpcomingEventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UpcomingEventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEvent(@PathVariable Long id, @RequestBody @Valid UpcomingEventRequest request) {
        return eventService.updateEvent(id, request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> patchEvent(@PathVariable Long id, @RequestBody UpcomingEventRequest request) {
        return eventService.patchEvent(id, request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }
}