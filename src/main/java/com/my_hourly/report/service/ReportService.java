package com.my_hourly.report.service;

import com.my_hourly.report.dto.AttendanceReportFilter;
import com.my_hourly.report.dto.EmployeeReportResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ReportService {

    /**
     * Returns paginated employee reports based on filters.
     */
    Page<EmployeeReportResponse> getReports(
            AttendanceReportFilter filter);

    /**
     * Returns a complete report for a single employee.
     */
    EmployeeReportResponse getEmployeeReport(
            Long employeeId,
            AttendanceReportFilter filter);

    /**
     * Generate PDF report.
     */
    ResponseEntity<Resource> generatePdfReport(
            AttendanceReportFilter filter);

    /**
     * Generate Excel report.
     */
    ResponseEntity<Resource> generateExcelReport(
            AttendanceReportFilter filter);
}