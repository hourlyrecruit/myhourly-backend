package com.my_hourly.attendance.controller;

import com.my_hourly.attendance.dto.*;
import com.my_hourly.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check-in")
    public AttendanceResponse checkIn(@RequestBody CheckInRequest request) {
        return attendanceService.checkIn(request);
    }
 
    @PostMapping("/check-out")
    public AttendanceResponse checkOut(@RequestBody CheckOutRequest request) {
        return attendanceService.checkOut(request);
    }


    @PostMapping("/break/start")
    public AttendanceBreakResponse startBreak(@RequestBody AttendanceBreakRequest request) {
        return attendanceService.startBreak(request);
    }


    @PutMapping("/break/end/{attendanceId}")
    public AttendanceBreakResponse endBreak(@PathVariable Long attendanceId) {
        return attendanceService.endBreak(attendanceId);
    }


    @GetMapping("/today")
    public AttendanceResponse getTodayAttendance() {
        return attendanceService.getTodayAttendance();
    } 


    @GetMapping("/history")
    public List<AttendanceResponse> getAttendanceHistory() {
        return attendanceService.getAttendanceHistory();
    }


    @GetMapping("/calendar")
    public List<AttendanceCalendarResponse> getAttendanceCalendar(
            												@RequestParam
            												@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    														LocalDate startDate,

    														@RequestParam
    														@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    														LocalDate endDate) {
        return attendanceService.getAttendanceCalendar(startDate,endDate);
    } 
    @GetMapping("/admin/employee/{employeeId}/today")
    //@PreAuthorize("hasAnyRole('HR','MANAGER')")
    public AttendanceResponse getEmployeeTodayAttendance(
            @PathVariable Long employeeId) {

        return attendanceService.getTodayAttendance(employeeId);
    }
    @GetMapping("/admin/employee/{employeeId}/history")
    //@PreAuthorize("hasAnyRole('HR','MANAGER')")
    public List<AttendanceResponse> getEmployeeAttendanceHistory(
            @PathVariable Long employeeId) {

        return attendanceService.getAttendanceHistory(employeeId);
    }
    @GetMapping("/admin/employee/{employeeId}/calendar")
    //@PreAuthorize("hasAnyRole('HR','MANAGER')")
    public List<AttendanceCalendarResponse> getEmployeeAttendanceCalendar(

            @PathVariable Long employeeId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return attendanceService.getAttendanceCalendar(
                employeeId,
                startDate,
                endDate); 
    }
    //@PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','MANAGER')")
    @GetMapping("/admin/today")
    public List<AttendanceResponse> getTodayAttendanceForAllEmployees() {

        return attendanceService.getTodayAttendanceForAllEmployees();

    }
    @GetMapping("/dashboard")
    public AttendanceDashboardResponse getDashboard() {
        return attendanceService.getDashboard();
    }

}