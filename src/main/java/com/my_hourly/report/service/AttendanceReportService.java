package com.my_hourly.report.service;

import com.my_hourly.report.dto.response.AttendanceReportPageResponse;
import com.my_hourly.report.dto.request.AttendanceReportRequest;


public interface AttendanceReportService {

    AttendanceReportPageResponse getAttendanceReport(
            AttendanceReportRequest request
    );

    byte[] exportAttendanceReport(
            AttendanceReportRequest request
    );

    byte[] exportAttendancePdf(
            AttendanceReportRequest request);

}
