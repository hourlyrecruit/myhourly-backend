package com.my_hourly.report.service;

import com.my_hourly.report.dto.request.LeaveReportRequest;
import com.my_hourly.report.dto.response.LeaveReportPageResponse;

public interface LeaveReportService {

    LeaveReportPageResponse getLeaveReport(
            LeaveReportRequest request);

    byte[] exportLeaveExcel(
            LeaveReportRequest request);

    byte[] exportLeavePdf(
            LeaveReportRequest request);
}
