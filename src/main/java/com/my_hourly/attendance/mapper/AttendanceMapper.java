package com.my_hourly.attendance.mapper;

import com.my_hourly.attendance.api.response.*;
import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceBreak;
import com.my_hourly.attendance.entity.BreakType;
import com.my_hourly.attendance.util.DateTimeUtil;
import com.my_hourly.attendance.util.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttendanceMapper {

    public CheckInResponse toCheckInResponse(Attendance attendance) {

        return CheckInResponse.builder()
                .id(attendance.getId())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .formattedCheckInTime(DateTimeUtil.formatTime(attendance.getCheckInTime()))
                .attendanceStatus(attendance.getAttendanceStatus())
                .lateMinutes(attendance.getLateMinutes())
                .employeeStatus(attendance.getEmployeeStatus())
                .build();
    }

    public CheckOutResponse toCheckOutResponse(Attendance attendance) {

        return CheckOutResponse.builder()
                .id(attendance.getId())
                .attendanceDate(attendance.getAttendanceDate())
                .checkOutTime(attendance.getCheckOutTime())
                .formattedCheckOutTime(DateTimeUtil.formatTime(attendance.getCheckInTime()))
                .earlyExitMinutes(attendance.getEarlyExitMinutes())
                .overtimeMinutes(attendance.getOvertimeMinutes())
                .attendanceStatus(attendance.getAttendanceStatus())
                .employeeStatus(attendance.getEmployeeStatus())
                .build();
    }

    public BreakStartResponse toStartBreakResponse(AttendanceBreak attendanceBreak){

        return BreakStartResponse.builder()
                .id(attendanceBreak.getId())
                .breakStartTime(DateTimeUtil.formatTime(attendanceBreak.getBreakStartTime()))
                .date(attendanceBreak.getAttendance().getAttendanceDate())
                .currentBreakType(attendanceBreak.getBreakType())
                .employeeStatus(attendanceBreak.getAttendance().getEmployeeStatus())
                .build();


    }

    public BreakEndResponse toEndBreakResponse(AttendanceBreak attendanceBreak){

        return BreakEndResponse.builder()
                .id(attendanceBreak.getId())
                .breakEndTime(DateTimeUtil.formatTime(attendanceBreak.getBreakStartTime()))
                .date(attendanceBreak.getAttendance().getAttendanceDate())
                .breakMinutes(attendanceBreak.getBreakMinutes())
                .employeeStatus(attendanceBreak.getAttendance().getEmployeeStatus())
                .build();


    }

    public AttendanceResponse toResponse(Attendance attendance) {

        return toResponse(attendance, null);
    }

    public AttendanceResponse toResponse(
            Attendance attendance,
            BreakType currentBreakType
    ) {

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .attendanceDate(attendance.getAttendanceDate())
                //.checkInTime(attendance.getCheckInTime())
                .checkInTime(DateTimeUtil.formatTime(attendance.getCheckInTime()))
                .checkOutTime(DateTimeUtil.formatTime(attendance.getCheckOutTime()))
                //.checkOutTime(attendance.getCheckOutTime())
//                .workingMinutes(attendance.getWorkingMinutes())
                .todayWorkingHours(TimeUtil.formatMinutes(attendance.getWorkingMinutes()))
//                .totalBreakMinutes(attendance.getTotalBreakMinutes())
                .todayBreakHours(TimeUtil.formatMinutes(attendance.getTotalBreakMinutes()))
                .attendanceStatus(attendance.getAttendanceStatus())
                .workingStatus(attendance.getEmployeeStatus())
                .currentBreakType(currentBreakType)
                .build();
    }

    public List<AttendanceResponse> toResponseList(List<Attendance> attendances) {

        return attendances.stream()
                .map(this::toResponse)
                .toList();
    }

    public AttendanceCalendarResponse toCalendarResponse(
            Attendance attendance
    ) {

        return AttendanceCalendarResponse.builder()
                .attendanceDate(attendance.getAttendanceDate())
                .attendanceStatus(attendance.getAttendanceStatus())
                .build();
    }

    public AttendanceDashboardResponse toDashboardResponse(
            Attendance attendance,
            BreakType currentBreakType
    ) {

        return AttendanceDashboardResponse.builder()
                .attendanceDate(attendance.getAttendanceDate())
                .checkedIn(attendance.getCheckInTime() != null)
                .checkedOut(attendance.getCheckOutTime() != null)
                .attendanceStatus(attendance.getAttendanceStatus())
                .employeeStatus(attendance.getEmployeeStatus())
                .currentBreakType(currentBreakType)
                .checkInTime(DateTimeUtil.formatTime(attendance.getCheckInTime()))
                .checkOutTime(DateTimeUtil.formatTime(attendance.getCheckOutTime()))
                .workingMinutes(attendance.getWorkingMinutes())
                .workingHours(TimeUtil.formatMinutes(attendance.getWorkingMinutes()))
                .breakMinutes(attendance.getTotalBreakMinutes())
                .breakHours(TimeUtil.formatMinutes(attendance.getTotalBreakMinutes()))
                .build();
    }

}
