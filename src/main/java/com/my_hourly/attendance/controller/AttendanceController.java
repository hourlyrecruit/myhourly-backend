package com.my_hourly.attendance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.attendance.dto.AttendanceBreakRequest;
import com.my_hourly.attendance.dto.AttendanceBreakResponse;
import com.my_hourly.attendance.dto.AttendanceCalendarResponse;
import com.my_hourly.attendance.dto.AttendanceDashboardResponse;
import com.my_hourly.attendance.dto.AttendanceResponse;
import com.my_hourly.attendance.dto.CheckInRequest;
import com.my_hourly.attendance.dto.CheckOutRequest;
import com.my_hourly.attendance.service.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;


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


    @GetMapping("/today/{employeeId}")
    public AttendanceResponse getTodayAttendance(@PathVariable Long employeeId) {
        return attendanceService.getTodayAttendance(employeeId);
    } 


    @GetMapping("/history/{employeeId}")
    public List<AttendanceResponse> getAttendanceHistory(@PathVariable Long employeeId) {
        return attendanceService.getAttendanceHistory(employeeId);
    }


    @GetMapping("/calendar/{employeeId}")
    public List<AttendanceCalendarResponse> getAttendanceCalendar(@PathVariable Long employeeId,
            												@RequestParam
            												@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    														LocalDate startDate,

    														@RequestParam
    														@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    														LocalDate endDate) {
        return attendanceService.getAttendanceCalendar(employeeId,startDate,endDate);
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
//    @GetMapping("/admin/dashboard")
//    //@PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
//    public AttendanceDashboardResponse getDashboardAttendance() {
//        return attendanceService.getDashboardAttendance();
//    }

}