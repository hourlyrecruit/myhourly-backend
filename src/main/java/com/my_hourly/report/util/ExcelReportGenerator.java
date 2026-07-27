package com.my_hourly.report.util;

import com.my_hourly.report.dto.AttendanceDetailResponse;
import com.my_hourly.report.dto.EmployeeReportResponse;
import com.my_hourly.report.dto.LeaveDetailResponse;
import com.my_hourly.report.entity.ReportType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportGenerator {

    public static ByteArrayInputStream generateReport(
            List<EmployeeReportResponse> reports,
            ReportType reportType) {

        try (
                XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.createSheet("Employee Report");

            switch (reportType) {

                case ATTENDANCE ->
                        generateAttendanceReport(sheet, reports);

                case LEAVE ->
                        generateLeaveReport(sheet, reports);

                case ATTENDANCE_LEAVE ->
                        generateAttendanceLeaveReport(sheet, reports);

                case ATTENDANCE_DETAIL ->
                        generateAttendanceDetailReport(sheet, reports);

                case LEAVE_DETAIL ->
                        generateLeaveDetailReport(sheet, reports);

                case ATTENDANCE_LEAVE_DETAIL ->
                        generateAttendanceLeaveDetailReport(sheet, reports);
            }

            for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate Excel report.", ex);
        }
    }
    private static void generateAttendanceReport(
            Sheet sheet,
            List<EmployeeReportResponse> reports) {

        createAttendanceHeader(sheet);

        int rowNum = 1;

        for (EmployeeReportResponse report : reports) {
            fillAttendanceRow(sheet, rowNum++, report);
        }
    }
    private static void createAttendanceHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Designation");

        header.createCell(4).setCellValue("Present Days");
        header.createCell(5).setCellValue("Absent Days");
        header.createCell(6).setCellValue("Half Days");
        header.createCell(7).setCellValue("Late Days");

        header.createCell(8).setCellValue("Working Minutes");
        header.createCell(9).setCellValue("Break Minutes");
        header.createCell(10).setCellValue("Late Minutes");
        header.createCell(11).setCellValue("Early Exit Minutes");
        header.createCell(12).setCellValue("Overtime Minutes");
        header.createCell(13).setCellValue("Attendance %");
    }
    private static void fillAttendanceRow(
            Sheet sheet,
            int rowNum,
            EmployeeReportResponse report) {

        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(report.getEmployeeCode());
        row.createCell(1).setCellValue(report.getEmployeeName());
        row.createCell(2).setCellValue(report.getDepartment());
        row.createCell(3).setCellValue(report.getDesignation());

        if (report.getAttendanceSummary() == null) {
            return;
        }

        row.createCell(4).setCellValue(report.getAttendanceSummary().getPresentDays());
        row.createCell(5).setCellValue(report.getAttendanceSummary().getAbsentDays());
        row.createCell(6).setCellValue(report.getAttendanceSummary().getHalfDays());
        row.createCell(7).setCellValue(report.getAttendanceSummary().getLateDays());

        row.createCell(8).setCellValue(report.getAttendanceSummary().getTotalWorkingMinutes());
        row.createCell(9).setCellValue(report.getAttendanceSummary().getTotalBreakMinutes());
        row.createCell(10).setCellValue(report.getAttendanceSummary().getTotalLateMinutes());
        row.createCell(11).setCellValue(report.getAttendanceSummary().getTotalEarlyExitMinutes());
        row.createCell(12).setCellValue(report.getAttendanceSummary().getTotalOvertimeMinutes());
        row.createCell(13).setCellValue(report.getAttendanceSummary().getAttendancePercentage());
    }

    private static void generateLeaveReport(
            Sheet sheet,
            List<EmployeeReportResponse> reports) {

        createLeaveHeader(sheet);

        int rowNum = 1;

        for (EmployeeReportResponse report : reports) {
            fillLeaveRow(sheet, rowNum++, report);
        }
    }
    private static void createLeaveHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Designation");

        header.createCell(4).setCellValue("Allocated Leaves");
        header.createCell(5).setCellValue("Used Leaves");
        header.createCell(6).setCellValue("Remaining Leaves");
        header.createCell(7).setCellValue("Expired Leaves");

        header.createCell(8).setCellValue("Pending Leaves");
        header.createCell(9).setCellValue("Approved Leaves");
        header.createCell(10).setCellValue("Rejected Leaves");
        header.createCell(11).setCellValue("Cancelled Leaves");
    }
    private static void fillLeaveRow(
            Sheet sheet,
            int rowNum,
            EmployeeReportResponse report) {

        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(report.getEmployeeCode());
        row.createCell(1).setCellValue(report.getEmployeeName());
        row.createCell(2).setCellValue(report.getDepartment());
        row.createCell(3).setCellValue(report.getDesignation());

        if (report.getLeaveSummary() == null) {
            return;
        }

        row.createCell(4).setCellValue(report.getLeaveSummary().getAllocatedLeaves());
        row.createCell(5).setCellValue(report.getLeaveSummary().getUsedLeaves());
        row.createCell(6).setCellValue(report.getLeaveSummary().getRemainingLeaves());
        row.createCell(7).setCellValue(report.getLeaveSummary().getExpiredLeaves());

        row.createCell(8).setCellValue(report.getLeaveSummary().getPendingLeaves());
        row.createCell(9).setCellValue(report.getLeaveSummary().getApprovedLeaves());
        row.createCell(10).setCellValue(report.getLeaveSummary().getRejectedLeaves());
        row.createCell(11).setCellValue(report.getLeaveSummary().getCancelledLeaves());
    }
    private static void generateAttendanceLeaveReport(
            Sheet sheet,
            List<EmployeeReportResponse> reports) {

        createAttendanceLeaveHeader(sheet);

        int rowNum = 1;

        for (EmployeeReportResponse report : reports) {
            fillAttendanceLeaveRow(sheet, rowNum++, report);
        }
    }
    private static void createAttendanceLeaveHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        // Employee Details
        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Designation");

        // Attendance Summary
        header.createCell(4).setCellValue("Present Days");
        header.createCell(5).setCellValue("Absent Days");
        header.createCell(6).setCellValue("Half Days");
        header.createCell(7).setCellValue("Late Days");
        header.createCell(8).setCellValue("Working Minutes");
        header.createCell(9).setCellValue("Break Minutes");
        header.createCell(10).setCellValue("Late Minutes");
        header.createCell(11).setCellValue("Early Exit Minutes");
        header.createCell(12).setCellValue("Overtime Minutes");
        header.createCell(13).setCellValue("Attendance %");

        // Leave Summary
        header.createCell(14).setCellValue("Allocated Leaves");
        header.createCell(15).setCellValue("Used Leaves");
        header.createCell(16).setCellValue("Remaining Leaves");
        header.createCell(17).setCellValue("Expired Leaves");
        header.createCell(18).setCellValue("Pending Leaves");
        header.createCell(19).setCellValue("Approved Leaves");
        header.createCell(20).setCellValue("Rejected Leaves");
        header.createCell(21).setCellValue("Cancelled Leaves");
    }
    private static void fillAttendanceLeaveRow(
            Sheet sheet,
            int rowNum,
            EmployeeReportResponse report) {

        Row row = sheet.createRow(rowNum);

        // Employee Details
        row.createCell(0).setCellValue(report.getEmployeeCode());
        row.createCell(1).setCellValue(report.getEmployeeName());
        row.createCell(2).setCellValue(report.getDepartment());
        row.createCell(3).setCellValue(report.getDesignation());

        // Attendance Summary
        if (report.getAttendanceSummary() != null) {

            row.createCell(4).setCellValue(report.getAttendanceSummary().getPresentDays());
            row.createCell(5).setCellValue(report.getAttendanceSummary().getAbsentDays());
            row.createCell(6).setCellValue(report.getAttendanceSummary().getHalfDays());
            row.createCell(7).setCellValue(report.getAttendanceSummary().getLateDays());
            row.createCell(8).setCellValue(report.getAttendanceSummary().getTotalWorkingMinutes());
            row.createCell(9).setCellValue(report.getAttendanceSummary().getTotalBreakMinutes());
            row.createCell(10).setCellValue(report.getAttendanceSummary().getTotalLateMinutes());
            row.createCell(11).setCellValue(report.getAttendanceSummary().getTotalEarlyExitMinutes());
            row.createCell(12).setCellValue(report.getAttendanceSummary().getTotalOvertimeMinutes());
            row.createCell(13).setCellValue(report.getAttendanceSummary().getAttendancePercentage());
        }

        // Leave Summary
        if (report.getLeaveSummary() != null) {

            row.createCell(14).setCellValue(report.getLeaveSummary().getAllocatedLeaves());
            row.createCell(15).setCellValue(report.getLeaveSummary().getUsedLeaves());
            row.createCell(16).setCellValue(report.getLeaveSummary().getRemainingLeaves());
            row.createCell(17).setCellValue(report.getLeaveSummary().getExpiredLeaves());
            row.createCell(18).setCellValue(report.getLeaveSummary().getPendingLeaves());
            row.createCell(19).setCellValue(report.getLeaveSummary().getApprovedLeaves());
            row.createCell(20).setCellValue(report.getLeaveSummary().getRejectedLeaves());
            row.createCell(21).setCellValue(report.getLeaveSummary().getCancelledLeaves());
        }
    }
    private static void generateAttendanceDetailReport(
            Sheet sheet,
            List<EmployeeReportResponse> reports) {

        createAttendanceDetailHeader(sheet);

        int rowNum = 1;

        for (EmployeeReportResponse report : reports) {

            rowNum = fillAttendanceDetailRows(
                    sheet,
                    rowNum,
                    report);
        }
    }
    private static void createAttendanceDetailHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Designation");

        header.createCell(4).setCellValue("Attendance Date");
        header.createCell(5).setCellValue("Check In");
        header.createCell(6).setCellValue("Check Out");
        header.createCell(7).setCellValue("Working Minutes");
        header.createCell(8).setCellValue("Break Minutes");
        header.createCell(9).setCellValue("Late Minutes");
        header.createCell(10).setCellValue("Early Exit Minutes");
        header.createCell(11).setCellValue("Overtime Minutes");
        header.createCell(12).setCellValue("Status");
        header.createCell(13).setCellValue("Break Details");
    }
    private static int fillAttendanceDetailRows(
            Sheet sheet,
            int rowNum,
            EmployeeReportResponse report) {

        if (report.getAttendanceDetails() == null
                || report.getAttendanceDetails().isEmpty()) {

            return rowNum;
        }

        for (AttendanceDetailResponse detail : report.getAttendanceDetails()) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(report.getEmployeeCode());
            row.createCell(1).setCellValue(report.getEmployeeName());
            row.createCell(2).setCellValue(report.getDepartment());
            row.createCell(3).setCellValue(report.getDesignation());

            row.createCell(4).setCellValue(
                    detail.getAttendanceDate() != null
                            ? detail.getAttendanceDate().toString()
                            : "");

            row.createCell(5).setCellValue(
                    detail.getCheckInTime() != null
                            ? detail.getCheckInTime().toString()
                            : "");

            row.createCell(6).setCellValue(
                    detail.getCheckOutTime() != null
                            ? detail.getCheckOutTime().toString()
                            : "");

            row.createCell(7).setCellValue(
                    detail.getWorkingMinutes() != null
                            ? detail.getWorkingMinutes()
                            : 0);

            row.createCell(8).setCellValue(
                    detail.getTotalBreakMinutes() != null
                            ? detail.getTotalBreakMinutes()
                            : 0);

            row.createCell(9).setCellValue(
                    detail.getLateMinutes() != null
                            ? detail.getLateMinutes()
                            : 0);

            row.createCell(10).setCellValue(
                    detail.getEarlyExitMinutes() != null
                            ? detail.getEarlyExitMinutes()
                            : 0);

            row.createCell(11).setCellValue(
                    detail.getOvertimeMinutes() != null
                            ? detail.getOvertimeMinutes()
                            : 0);

            row.createCell(12).setCellValue(
                    detail.getAttendanceStatus() != null
                            ? detail.getAttendanceStatus().name()
                            : "");

            String breakDetails = "";

            if (detail.getBreaks() != null && !detail.getBreaks().isEmpty()) {

                breakDetails = detail.getBreaks()
                        .stream()
                        .map(b -> b.getBreakType()
                                + " (" + b.getBreakDurationMinutes() + " mins)")
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            }

            row.createCell(13).setCellValue(breakDetails);
        }

        return rowNum;
    }
    private static void generateLeaveDetailReport(
            Sheet sheet,
            List<EmployeeReportResponse> reports) {

        createLeaveDetailHeader(sheet);

        int rowNum = 1;

        for (EmployeeReportResponse report : reports) {

            rowNum = fillLeaveDetailRows(
                    sheet,
                    rowNum,
                    report);
        }
    }
    private static void createLeaveDetailHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Designation");

        header.createCell(4).setCellValue("Leave Type");
        header.createCell(5).setCellValue("Start Date");
        header.createCell(6).setCellValue("End Date");
        header.createCell(7).setCellValue("Total Days");
        header.createCell(8).setCellValue("Status");
        header.createCell(9).setCellValue("Reason");
    }
    private static int fillLeaveDetailRows(
            Sheet sheet,
            int rowNum,
            EmployeeReportResponse report) {

        if (report.getLeaveDetails() == null
                || report.getLeaveDetails().isEmpty()) {

            return rowNum;
        }

        for (LeaveDetailResponse detail : report.getLeaveDetails()) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(report.getEmployeeCode());
            row.createCell(1).setCellValue(report.getEmployeeName());
            row.createCell(2).setCellValue(report.getDepartment());
            row.createCell(3).setCellValue(report.getDesignation());

            row.createCell(4).setCellValue(
                    detail.getLeaveType() != null
                            ? detail.getLeaveType()
                            : "");

            row.createCell(5).setCellValue(
                    detail.getStartDate() != null
                            ? detail.getStartDate().toString()
                            : "");

            row.createCell(6).setCellValue(
                    detail.getEndDate() != null
                            ? detail.getEndDate().toString()
                            : "");

            row.createCell(7).setCellValue(
                    detail.getTotalDays() != null
                            ? detail.getTotalDays()
                            : 0);

            row.createCell(8).setCellValue(
                    detail.getStatus() != null
                            ? detail.getStatus().name()
                            : "");

            row.createCell(9).setCellValue(
                    detail.getReason() != null
                            ? detail.getReason()
                            : "");
        }

        return rowNum;
    }
    private static void generateAttendanceLeaveDetailReport(
            Sheet sheet,
            List<EmployeeReportResponse> reports) {

        createAttendanceLeaveDetailHeader(sheet);

        int rowNum = 1;

        for (EmployeeReportResponse report : reports) {

            rowNum = fillAttendanceLeaveDetailRows(
                    sheet,
                    rowNum,
                    report);
        }
    }
    private static void createAttendanceLeaveDetailHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Designation");

        header.createCell(4).setCellValue("Record Type");

        header.createCell(5).setCellValue("Date");
        header.createCell(6).setCellValue("Check In / Leave Type");
        header.createCell(7).setCellValue("Check Out / Start Date");
        header.createCell(8).setCellValue("Working Minutes / End Date");
        header.createCell(9).setCellValue("Break Minutes / Total Days");
        header.createCell(10).setCellValue("Late Minutes");
        header.createCell(11).setCellValue("Early Exit Minutes");
        header.createCell(12).setCellValue("Overtime Minutes");
        header.createCell(13).setCellValue("Status");
        header.createCell(14).setCellValue("Reason / Break Details");
    }
    private static void fillEmployeeRow(
            Row row,
            EmployeeReportResponse report) {

        row.createCell(0).setCellValue(report.getEmployeeCode());
        row.createCell(1).setCellValue(report.getEmployeeName());
        row.createCell(2).setCellValue(report.getDepartment());
        row.createCell(3).setCellValue(report.getDesignation());

        if (report.getAttendanceSummary() != null) {

            row.createCell(4).setCellValue(
                    report.getAttendanceSummary().getPresentDays());

            row.createCell(5).setCellValue(
                    report.getAttendanceSummary().getAbsentDays());

            row.createCell(6).setCellValue(
                    report.getAttendanceSummary().getHalfDays());

            row.createCell(7).setCellValue(
                    report.getAttendanceSummary().getLateDays());

            row.createCell(8).setCellValue(
                    report.getAttendanceSummary().getTotalWorkingMinutes());

            row.createCell(9).setCellValue(
                    report.getAttendanceSummary().getTotalBreakMinutes());

            row.createCell(10).setCellValue(
                    report.getAttendanceSummary().getTotalLateMinutes());

            row.createCell(11).setCellValue(
                    report.getAttendanceSummary().getTotalEarlyExitMinutes());

            row.createCell(12).setCellValue(
                    report.getAttendanceSummary().getTotalOvertimeMinutes());

            row.createCell(13).setCellValue(
                    report.getAttendanceSummary().getAttendancePercentage());
        }

        if (report.getLeaveSummary() != null) {

            row.createCell(14).setCellValue(report.getLeaveSummary().getAllocatedLeaves());
            row.createCell(15).setCellValue(report.getLeaveSummary().getUsedLeaves());
            row.createCell(16).setCellValue(report.getLeaveSummary().getRemainingLeaves());
            row.createCell(17).setCellValue(report.getLeaveSummary().getPendingLeaves());
        }
    }



    private static int fillAttendanceLeaveDetailRows(
            Sheet sheet,
            int rowNum,
            EmployeeReportResponse report) {

        // Attendance Records
        if (report.getAttendanceDetails() != null) {

            for (AttendanceDetailResponse detail : report.getAttendanceDetails()) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(report.getEmployeeCode());
                row.createCell(1).setCellValue(report.getEmployeeName());
                row.createCell(2).setCellValue(report.getDepartment());
                row.createCell(3).setCellValue(report.getDesignation());

                row.createCell(4).setCellValue("ATTENDANCE");

                row.createCell(5).setCellValue(
                        detail.getAttendanceDate() != null
                                ? detail.getAttendanceDate().toString()
                                : "");

                row.createCell(6).setCellValue(
                        detail.getCheckInTime() != null
                                ? detail.getCheckInTime().toString()
                                : "");

                row.createCell(7).setCellValue(
                        detail.getCheckOutTime() != null
                                ? detail.getCheckOutTime().toString()
                                : "");

                row.createCell(8).setCellValue(
                        detail.getWorkingMinutes() != null
                                ? detail.getWorkingMinutes()
                                : 0);

                row.createCell(9).setCellValue(
                        detail.getTotalBreakMinutes() != null
                                ? detail.getTotalBreakMinutes()
                                : 0);

                row.createCell(10).setCellValue(
                        detail.getLateMinutes() != null
                                ? detail.getLateMinutes()
                                : 0);

                row.createCell(11).setCellValue(
                        detail.getEarlyExitMinutes() != null
                                ? detail.getEarlyExitMinutes()
                                : 0);

                row.createCell(12).setCellValue(
                        detail.getOvertimeMinutes() != null
                                ? detail.getOvertimeMinutes()
                                : 0);

                row.createCell(13).setCellValue(
                        detail.getAttendanceStatus() != null
                                ? detail.getAttendanceStatus().name()
                                : "");

                String breaks = "";

                if (detail.getBreaks() != null && !detail.getBreaks().isEmpty()) {

                    breaks = detail.getBreaks().stream()
                            .map(b -> b.getBreakType() +
                                    " (" + b.getBreakDurationMinutes() + " mins)")
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                }

                row.createCell(14).setCellValue(breaks);
            }
        }

        // Leave Records
        if (report.getLeaveDetails() != null) {

            for (LeaveDetailResponse detail : report.getLeaveDetails()) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(report.getEmployeeCode());
                row.createCell(1).setCellValue(report.getEmployeeName());
                row.createCell(2).setCellValue(report.getDepartment());
                row.createCell(3).setCellValue(report.getDesignation());

                row.createCell(4).setCellValue("LEAVE");

                row.createCell(5).setCellValue("");
                row.createCell(6).setCellValue(detail.getLeaveType());
                row.createCell(7).setCellValue(
                        detail.getStartDate() != null
                                ? detail.getStartDate().toString()
                                : "");

                row.createCell(8).setCellValue(
                        detail.getEndDate() != null
                                ? detail.getEndDate().toString()
                                : "");

                row.createCell(9).setCellValue(
                        detail.getTotalDays() != null
                                ? detail.getTotalDays()
                                : 0);

                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue("");
                row.createCell(12).setCellValue("");

                row.createCell(13).setCellValue(
                        detail.getStatus() != null
                                ? detail.getStatus().name()
                                : "");

                row.createCell(14).setCellValue(
                        detail.getReason() != null
                                ? detail.getReason()
                                : "");
            }
        }

        return rowNum;
    }
}