package com.my_hourly.report.controller;

import com.my_hourly.report.dto.AttendanceReportFilter;
import com.my_hourly.report.dto.EmployeeReportResponse;
import com.my_hourly.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    /**
     * Get reports for all employees
     * Supports:
     * - department filter
     * - employee name filter
     * - employee id filter
     * - attendance/leave filter
     * - date/month/year filter
     */
    @PostMapping("/employees")
    public ResponseEntity<Page<EmployeeReportResponse>> getEmployeeReports(
            @RequestBody AttendanceReportFilter filter) {


        Page<EmployeeReportResponse> reports =
                reportService.getReports(filter);


        return ResponseEntity.ok(reports);
    }



    /**
     * Get single employee report
     */
    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeReportResponse> getEmployeeReport(
            @PathVariable Long employeeId,
            @RequestBody AttendanceReportFilter filter) {


        EmployeeReportResponse report =
                reportService.getEmployeeReport(
                        employeeId,
                        filter
                );


        return ResponseEntity.ok(report);
    }




    /**
     * Download PDF Report
     */
    @PostMapping("/download/pdf")
    public ResponseEntity<Resource> downloadPdf(
            @RequestBody AttendanceReportFilter filter) {


        return reportService.generatePdfReport(filter);

    }





    /**
     * Download Excel Report
     */
    @PostMapping("/download/excel")
    public ResponseEntity<Resource> downloadExcel(
            @RequestBody AttendanceReportFilter filter) {


        return reportService.generateExcelReport(filter);

    }

}