package com.my_hourly.attendance.service;

import com.my_hourly.attendance.api.request.BreakStartRequest;
import com.my_hourly.attendance.api.request.CheckInRequest;
import com.my_hourly.attendance.api.request.CheckOutRequest;
import com.my_hourly.attendance.api.response.*;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.common.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    CheckInResponse checkIn(CheckInRequest request);
    CheckOutResponse checkOut(CheckOutRequest request);

    BreakStartResponse startBreak(BreakStartRequest request);

    BreakEndResponse endBreak();

    AttendanceResponse getTodayAttendance();

    PageResponse<AttendanceResponse> getAttendanceHistory(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            LocalDate fromDate,
            LocalDate toDate,
            AttendanceStatus status
    );

    AttendanceMonthlySummaryResponse getMonthlySummary(
            Integer month,
            Integer year
    );

    List<AttendanceCalendarResponse> getAttendanceCalendar(
            Integer month,
            Integer year
    );

    AttendanceDashboardResponse getAttendanceDashboard();
}
