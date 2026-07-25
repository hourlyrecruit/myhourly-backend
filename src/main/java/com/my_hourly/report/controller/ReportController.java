package com.my_hourly.report.controller;

import com.my_hourly.report.dto.ApiResponse;
import com.my_hourly.report.dto.EmployeeReportResponse;
import com.my_hourly.report.dto.ReportRequest;
import com.my_hourly.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Get report for a single employee.
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR_ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<EmployeeReportResponse> getEmployeeReport(@PathVariable Long employeeId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        return ResponseEntity.ok(
                reportService.getEmployeeReport(
                        employeeId,
                        fromDate,
                        toDate));
    }
    /**
     * Get all employee reports without generating file.
     */
    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('HR_ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<EmployeeReportResponse>> getAllEmployeeReports(@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
        return ResponseEntity.ok(
                reportService.getEmployeeReports(
                        fromDate,
                        toDate));
    }

    /**
     * Generate PDF report.
     * Supports:
     * - All employees
     * - Selected employees
     * - Attendance
     * - Leave
     * - Attendance + Leave
     */
    @PostMapping("/pdf")
    @PreAuthorize("hasAnyRole('HR_ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> generatePdfReport(@RequestBody ReportRequest request) {
        return reportService.generatePdfReport(request);
    }

    /**
     * Generate Excel report.
     * Supports:
     * - All employees
     * - Selected employees
     * - Attendance
     * - Leave
     * - Attendance + Leave
     */
    @PostMapping("/excel")
    @PreAuthorize("hasAnyRole('HR_ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> generateExcelReport(@RequestBody ReportRequest request) {
        return reportService.generateExcelReport(request);
    }

}