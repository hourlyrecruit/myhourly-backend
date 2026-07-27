package com.my_hourly.report.controller;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.report.dto.request.AttendanceReportRequest;
import com.my_hourly.report.dto.request.LeaveReportRequest;
import com.my_hourly.report.dto.response.AttendanceReportPageResponse;
import com.my_hourly.report.dto.response.LeaveReportPageResponse;
import com.my_hourly.report.service.AttendanceReportService;
import com.my_hourly.report.service.LeaveReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for generating and exporting attendance and leave reports
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated
@Tag(name = "15-Reports", description = "Reports & Analytics APIs for HR and Managers")
public class ReportController {

    private final AttendanceReportService attendanceReportService;
    private final LeaveReportService leaveReportService;

    @GetMapping("/attendance")
    @Operation(
        summary = "Get Attendance Report",
        description = "Retrieve attendance report with filters. Support multiple formats: JSON (default), Excel, PDF"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved/exported attendance report"
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - requires HR or Manager role")
    })
    @PreAuthorize("hasAnyRole('HR','MANAGER')")
    public ResponseEntity<?> getAttendanceReport(
            @Parameter(description = "Response format: json, excel, or pdf", example = "json")
            @RequestParam(defaultValue = "json") String format,
            
            @Parameter(description = "Filter by Employee ID", example = "1")
            @RequestParam(required = false) Long employeeId,
            
            @Parameter(description = "Filter by Employee Name (partial match)", example = "John")
            @RequestParam(required = false) String employeeName,
            
            @Parameter(description = "Filter by Department ID", example = "5")
            @RequestParam(required = false) Long departmentId,
            
            @Parameter(description = "Filter by Attendance Status", example = "PRESENT")
            @RequestParam(required = false) AttendanceStatus attendanceStatus,
            
            @Parameter(description = "Start Date (YYYY-MM-DD)", example = "2026-07-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End Date (YYYY-MM-DD)", example = "2026-07-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Month (1-12)", example = "7")
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            
            @Parameter(description = "Year", example = "2026")
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
            
            @Parameter(description = "Page number (0-indexed, only for JSON format)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Page size (only for JSON format)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size,
            
            @Parameter(description = "Sort field", example = "attendanceDate")
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDir) {

        // Build request object
        AttendanceReportRequest request = new AttendanceReportRequest();
        request.setEmployeeId(employeeId);
        request.setEmployeeName(employeeName);
        request.setDepartmentId(departmentId);
        request.setAttendanceStatus(attendanceStatus);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setMonth(month);
        request.setYear(year);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);

        // Handle different formats
        switch (format.toLowerCase()) {
            case "excel":
                byte[] excel = attendanceReportService.exportAttendanceReport(request);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=attendance-report.xlsx")
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(excel);

            case "pdf":
                byte[] pdf = attendanceReportService.exportAttendancePdf(request);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=attendance-report.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdf);

            case "json":
            default:
                AttendanceReportPageResponse response = 
                        attendanceReportService.getAttendanceReport(request);
                return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/leave")
    @Operation(
        summary = "Get Leave Report",
        description = "Retrieve leave report with filters. Support multiple formats: JSON (default), Excel, PDF"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved/exported leave report"
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - requires HR or Manager role")
    })
    @PreAuthorize("hasAnyRole('HR','MANAGER')")
    public ResponseEntity<?> getLeaveReport(
            @Parameter(description = "Response format: json, excel, or pdf", example = "json")
            @RequestParam(defaultValue = "json") String format,
            
            @Parameter(description = "Filter by Employee ID", example = "1")
            @RequestParam(required = false) Long employeeId,
            
            @Parameter(description = "Filter by Employee Name (partial match)", example = "John")
            @RequestParam(required = false) String employeeName,
            
            @Parameter(description = "Filter by Department ID", example = "5")
            @RequestParam(required = false) Long departmentId,
            
            @Parameter(description = "Filter by Leave Status", example = "APPROVED")
            @RequestParam(required = false) LeaveStatus leaveStatus,
            
            @Parameter(description = "Start Date (YYYY-MM-DD)", example = "2026-07-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End Date (YYYY-MM-DD)", example = "2026-07-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Month (1-12)", example = "7")
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            
            @Parameter(description = "Year", example = "2026")
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
            
            @Parameter(description = "Page number (0-indexed, only for JSON format)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Page size (only for JSON format)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size,
            
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDir) {

        // Build request object
        LeaveReportRequest request = new LeaveReportRequest();
        request.setEmployeeId(employeeId);
        request.setEmployeeName(employeeName);
        request.setDepartmentId(departmentId);
        request.setLeaveStatus(leaveStatus);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setMonth(month);
        request.setYear(year);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);

        // Handle different formats
        switch (format.toLowerCase()) {
            case "excel":
                byte[] excel = leaveReportService.exportLeaveExcel(request);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=leave-report.xlsx")
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(excel);

            case "pdf":
                byte[] pdf = leaveReportService.exportLeavePdf(request);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=leave-report.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdf);

            case "json":
            default:
                LeaveReportPageResponse response = 
                        leaveReportService.getLeaveReport(request);
                return ResponseEntity.ok(response);
        }
    }

}
