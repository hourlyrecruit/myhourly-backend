package com.my_hourly.report.dto;

import com.my_hourly.report.entity.ReportFormat;
import com.my_hourly.report.entity.ReportPeriod;
import com.my_hourly.report.entity.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReportRequest {

    private List<Long> employeeIds;
    //for custom
    private LocalDate fromDate;
    private LocalDate toDate;
    private ReportType reportType;
    private ReportPeriod period;
    private ReportFormat reportFormat;

}