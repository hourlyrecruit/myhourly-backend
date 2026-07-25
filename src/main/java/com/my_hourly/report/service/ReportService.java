package com.my_hourly.report.service;

import com.my_hourly.report.dto.ApiResponse;
import com.my_hourly.report.dto.EmployeeReportResponse;
import com.my_hourly.report.dto.ReportRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    // Dashboard/API
    EmployeeReportResponse getEmployeeReport(
            Long employeeId,
            LocalDate fromDate,
            LocalDate toDate);

    List<EmployeeReportResponse> getEmployeeReports(
            LocalDate fromDate,
            LocalDate toDate);

    @Transactional
    ResponseEntity<ApiResponse> generatePdfReport(ReportRequest request);

    @Transactional
    ResponseEntity<ApiResponse> generateExcelReport(ReportRequest request);
}