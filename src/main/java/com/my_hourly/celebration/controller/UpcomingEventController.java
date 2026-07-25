package com.my_hourly.celebration.controller;

import com.my_hourly.celebration.dto.UpcomingEventRequest;
import com.my_hourly.celebration.dto.UpcomingEventResponse;
import com.my_hourly.celebration.service.UpcomingEventService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "17-Upcoming Event Management")
public class UpcomingEventController {

    private final UpcomingEventService eventService;
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PostMapping
    @Operation(summary = "Create new event (HR Admin only)")
    public ResponseEntity<ApiResponse> createEvent(@RequestBody @Valid UpcomingEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get upcoming events, Access: Any Logged In User")
    public ResponseEntity<List<UpcomingEventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get event by ID, Access: Any Logged In User")
    public ResponseEntity<UpcomingEventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update event by ID (HR Admin only)")
    public ResponseEntity<ApiResponse> updateEvent(@PathVariable Long id, @RequestBody @Valid UpcomingEventRequest request) {
        return eventService.updateEvent(id, request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update event by ID (HR Admin only)")
    public ResponseEntity<ApiResponse> patchEvent(@PathVariable Long id, @RequestBody UpcomingEventRequest request) {
        return eventService.patchEvent(id, request);
    }
    @PreAuthorize("hasRole('HR_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event by ID (HR Admin only)")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }
}