package com.my_hourly.attendance.dto;

import java.time.LocalDate;

import com.my_hourly.attendance.enums.AttendanceStatus;

public class AttendanceCalendarResponse {

    private LocalDate attendanceDate;
    private AttendanceStatus attendanceStatus;

    public AttendanceCalendarResponse() {
    }

    public AttendanceCalendarResponse(LocalDate attendanceDate, AttendanceStatus attendanceStatus) {
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}