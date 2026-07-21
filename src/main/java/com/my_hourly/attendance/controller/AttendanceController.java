package com.my_hourly.attendance.controller;

import com.my_hourly.attendance.api.request.BreakStartRequest;
import com.my_hourly.attendance.api.request.CheckInRequest;
import com.my_hourly.attendance.api.request.CheckOutRequest;
import com.my_hourly.attendance.api.response.*;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.service.AttendanceService;
import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.common.payload.response.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "C4. Attendance", description = "Attendance Management APIs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('attendance:create')")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            @Valid @RequestBody CheckInRequest request) {

        CheckInResponse response = attendanceService.checkIn(request);

        return ResponseEntity.ok(
                ApiResponse.<CheckInResponse>builder()
                        .success(true)
                        .message("Checked in successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/break-start")
    @PreAuthorize("hasAuthority('attendance:create')")
    public ResponseEntity<ApiResponse<BreakStartResponse>> startBreak(
            @Valid @RequestBody BreakStartRequest request) {

        BreakStartResponse response = attendanceService.startBreak(request);

        return ResponseEntity.ok(
                ApiResponse.<BreakStartResponse>builder()
                        .success(true)
                        .message("Break started successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/break-end")
    @PreAuthorize("hasAuthority('attendance:create')")
    public ResponseEntity<ApiResponse<BreakEndResponse>> endBreak() {

        BreakEndResponse response = attendanceService.endBreak();

        return ResponseEntity.ok(
                ApiResponse.<BreakEndResponse>builder()
                        .success(true)
                        .message("Break ended successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAuthority('attendance:create')")
    public ResponseEntity<ApiResponse<CheckOutResponse>> checkOut(
            @Valid @RequestBody CheckOutRequest request) {

        CheckOutResponse response = attendanceService.checkOut(request);

        return ResponseEntity.ok(
                ApiResponse.<CheckOutResponse>builder()
                        .success(true)
                        .message("Checked out successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

//    @GetMapping("/todayBreak")
//    @PreAuthorize("hasAuthority('attendance:view')")
//    public ResponseEntity<ApiResponse<AttendanceResponse>> getTodayAttendance() {
//
//        AttendanceResponse response = attendanceService.getTodayAttendance();
//
//        return ResponseEntity.ok(
//                ApiResponse.<AttendanceResponse>builder()
//                        .success(true)
//                        .message("Today's attendance fetched successfully.")
//                        .data(response)
//                        .build()
//        );
//    }

    @GetMapping("/today")
    @PreAuthorize("hasAuthority('attendance:view')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getTodayAttendance() {

        AttendanceResponse response = attendanceService.getTodayAttendance();

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponse>builder()
                        .success(true)
                        .message("Today's attendance fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('attendance:view')")
    public ResponseEntity<ApiResponse<PageResponse<AttendanceResponse>>> getAttendanceHistory(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "attendanceDate") String sortBy,

            @RequestParam(defaultValue = "desc") String sortDirection,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            AttendanceStatus status
    ) {

        PageResponse<AttendanceResponse> response =
                attendanceService.getAttendanceHistory(
                        page,
                        size,
                        sortBy,
                        sortDirection,
                        fromDate,
                        toDate,
                        status
                );

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<AttendanceResponse>>builder()
                        .success(true)
                        .message("Attendance history fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/monthly-summary")
    @PreAuthorize("hasAuthority('attendance:view')")
    public ResponseEntity<ApiResponse<AttendanceMonthlySummaryResponse>> getMonthlySummary(

            @RequestParam Integer month,

            @RequestParam Integer year
    ) {

        AttendanceMonthlySummaryResponse response =
                attendanceService.getMonthlySummary(month, year);

        return ResponseEntity.ok(
                ApiResponse.<AttendanceMonthlySummaryResponse>builder()
                        .success(true)
                        .message("Monthly attendance summary fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('attendance:view')")
    public ResponseEntity<ApiResponse<List<AttendanceCalendarResponse>>> getAttendanceCalendar(

            @RequestParam(required = false) Integer month,

            @RequestParam(required = false) Integer year
    ) {

        List<AttendanceCalendarResponse> response =
                attendanceService.getAttendanceCalendar(
                        month,
                        year
                );

        return ResponseEntity.ok(
                ApiResponse.<List<AttendanceCalendarResponse>>builder()
                        .success(true)
                        .message("Attendance calendar fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('attendance:view')")
    public ResponseEntity<ApiResponse<AttendanceDashboardResponse>> getDashboard() {

        AttendanceDashboardResponse response =
                attendanceService.getAttendanceDashboard();

        return ResponseEntity.ok(
                ApiResponse.<AttendanceDashboardResponse>builder()
                        .success(true)
                        .message("Attendance dashboard fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}