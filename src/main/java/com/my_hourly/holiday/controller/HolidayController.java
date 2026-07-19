package com.my_hourly.holiday.controller;


import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.holiday.api.request.CreateHolidayRequest;
import com.my_hourly.holiday.api.request.UpdateHolidayRequest;
import com.my_hourly.holiday.api.response.HolidayCalendarResponse;
import com.my_hourly.holiday.api.response.HolidayResponse;
import com.my_hourly.holiday.entity.HolidayType;
import com.my_hourly.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday", description = "Holiday Management APIs")
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('holiday:create')")
    public ResponseEntity<ApiResponse<HolidayResponse>> createHoliday(
            @Valid @RequestBody CreateHolidayRequest request) {

        HolidayResponse response =
                holidayService.createHoliday(request);

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponse>builder()
                        .success(true)
                        .message("Holiday created successfully.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{holidayId}")
    @PreAuthorize("hasAnyAuthority('holiday:update')")
    public ResponseEntity<ApiResponse<HolidayResponse>> updateHoliday(
            @PathVariable Long holidayId,
            @Valid @RequestBody UpdateHolidayRequest request) {

        HolidayResponse response =
                holidayService.updateHoliday(holidayId, request);

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponse>builder()
                        .success(true)
                        .message("Holiday updated successfully.")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{holidayId}")
    @PreAuthorize("hasAnyAuthority('holiday:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(
            @PathVariable Long holidayId) {

        holidayService.deleteHoliday(holidayId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Holiday deleted successfully.")
                        .build()
        );
    }

    @GetMapping("/{holidayId}")
    @PreAuthorize("hasAuthority('holiday:view')")
    public ResponseEntity<ApiResponse<HolidayResponse>> getHolidayById(
            @PathVariable Long holidayId) {

        HolidayResponse response =
                holidayService.getHolidayById(holidayId);

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponse>builder()
                        .success(true)
                        .message("Holiday fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('holiday:view')")
    public ResponseEntity<ApiResponse<PageResponse<HolidayResponse>>> getAllHolidays(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "holidayDate") String sortBy,

            @RequestParam(defaultValue = "asc") String sortDirection,

            @RequestParam(required = false) String holidayName,

            @RequestParam(required = false) HolidayType holidayType,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) Boolean active
    ) {

        PageResponse<HolidayResponse> response =
                holidayService.getAllHolidays(
                        page,
                        size,
                        sortBy,
                        sortDirection,
                        holidayName,
                        holidayType,
                        fromDate,
                        toDate,
                        active
                );

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<HolidayResponse>>builder()
                        .success(true)
                        .message("Holidays fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('holiday:view')")
    public ResponseEntity<ApiResponse<List<HolidayCalendarResponse>>> getHolidayCalendar(

            @RequestParam(required = false) Integer month,

            @RequestParam(required = false) Integer year
    ) {

        List<HolidayCalendarResponse> response =
                holidayService.getHolidayCalendar(month, year);

        return ResponseEntity.ok(
                ApiResponse.<List<HolidayCalendarResponse>>builder()
                        .success(true)
                        .message("Holiday calendar fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAuthority('holiday:view')")
    public ResponseEntity<ApiResponse<List<HolidayResponse>>> getUpcomingHolidays() {

        List<HolidayResponse> response =
                holidayService.getUpcomingHolidays();

        return ResponseEntity.ok(
                ApiResponse.<List<HolidayResponse>>builder()
                        .success(true)
                        .message("Upcoming holidays fetched successfully.")
                        .data(response)
                        .build()
        );
    }

}