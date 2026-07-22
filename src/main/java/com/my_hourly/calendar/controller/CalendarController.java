package com.my_hourly.calendar.controller;

import com.my_hourly.calendar.api.response.CalendarResponse;
import com.my_hourly.calendar.enums.CalendarEventType;
import com.my_hourly.calendar.enums.CalendarView;
import com.my_hourly.calendar.service.CalendarService;
import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.holiday.api.response.HolidayResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;


@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Validated
@Tag(name = "Calendar", description = "Calendar APIs")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "Get Calendar Events")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<CalendarResponse>> getCalendar(

            @Parameter(description = "Month (1-12)")
            @RequestParam
            @Min(1)
            @Max(12)
            Integer month,

            @Parameter(description = "Year")
            @RequestParam
            @Min(2000)
            Integer year,

            @Parameter(description = "Calendar View")
            @RequestParam(defaultValue = "PERSONAL")
            CalendarView view,

            @Parameter(description = "Event Types Filter")
            @RequestParam(required = false)
            List<CalendarEventType> eventTypes
    ) {

        CalendarResponse response = calendarService.getCalendar(
                month,
                year,
                view,
                eventTypes
        );


        return ResponseEntity.ok(
                ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Holidays fetched successfully.")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build()
        );
    }

}