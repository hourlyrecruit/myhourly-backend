package com.my_hourly.report.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.my_hourly.report.dto.AttendanceDetailResponse;
import com.my_hourly.report.dto.EmployeeReportResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.my_hourly.report.dto.LeaveDetailResponse;
import com.my_hourly.report.entity.ReportType;

public class PdfReportGenerator {

    public static ByteArrayInputStream generateReport(
            List<EmployeeReportResponse> reports, ReportType reportType) {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 18);

            Font headerFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 12);

            Font normalFont = FontFactory.getFont(
                    FontFactory.HELVETICA, 11);

            for (int i = 0; i < reports.size(); i++) {

                EmployeeReportResponse report = reports.get(i);

                Paragraph title = new Paragraph(
                        "Employee Report",
                        titleFont);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);

                document.add(title);

                // Employee Information
                document.add(createEmployeeInfoTable(
                        report,
                        headerFont,
                        normalFont));

                document.add(new Paragraph(" "));

                switch (reportType) {

                    case ATTENDANCE ->
                            document.add(createAttendanceSummaryTable(
                                    report,
                                    headerFont,
                                    normalFont));

                    case LEAVE ->
                            document.add(createLeaveSummaryTable(
                                    report,
                                    headerFont,
                                    normalFont));

                    case ATTENDANCE_LEAVE -> {

                        document.add(createAttendanceSummaryTable(
                                report,
                                headerFont,
                                normalFont));

                        document.add(new Paragraph(" "));

                        document.add(createLeaveSummaryTable(
                                report,
                                headerFont,
                                normalFont));
                    }

                    case ATTENDANCE_DETAIL ->
                            document.add(createAttendanceDetailTable(
                                    report,
                                    headerFont,
                                    normalFont));

                    case LEAVE_DETAIL ->
                            document.add(createLeaveDetailTable(
                                    report,
                                    headerFont,
                                    normalFont));

                    case ATTENDANCE_LEAVE_DETAIL -> {

                        document.add(createAttendanceDetailTable(
                                report,
                                headerFont,
                                normalFont));

                        document.add(new Paragraph(" "));

                        document.add(createLeaveDetailTable(
                                report,
                                headerFont,
                                normalFont));
                    }
                }

                if (i < reports.size() - 1) {
                    document.newPage();
                }
            }

            document.close();

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to generate PDF report.",
                    ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
    private static PdfPTable createAttendanceDetailTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        PdfPCell title = new PdfPCell(
                new Phrase("Attendance Details", headerFont));

        title.setColspan(10);
        title.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(title);

        table.addCell(new Phrase("Date", headerFont));
        table.addCell(new Phrase("Check In", headerFont));
        table.addCell(new Phrase("Check Out", headerFont));
        table.addCell(new Phrase("Working", headerFont));
        table.addCell(new Phrase("Break", headerFont));
        table.addCell(new Phrase("Late", headerFont));
        table.addCell(new Phrase("Early Exit", headerFont));
        table.addCell(new Phrase("Overtime", headerFont));
        table.addCell(new Phrase("Status", headerFont));
        table.addCell(new Phrase("Break Details", headerFont));

        if (report.getAttendanceDetails() != null) {

            for (AttendanceDetailResponse detail : report.getAttendanceDetails()) {

                table.addCell(detail.getAttendanceDate() != null
                        ? detail.getAttendanceDate().toString()
                        : "-");

                table.addCell(detail.getCheckInTime() != null
                        ? detail.getCheckInTime().toString()
                        : "-");

                table.addCell(detail.getCheckOutTime() != null
                        ? detail.getCheckOutTime().toString()
                        : "-");

                table.addCell(String.valueOf(
                        detail.getWorkingMinutes() != null
                                ? detail.getWorkingMinutes()
                                : 0));

                table.addCell(String.valueOf(
                        detail.getTotalBreakMinutes() != null
                                ? detail.getTotalBreakMinutes()
                                : 0));

                table.addCell(String.valueOf(
                        detail.getLateMinutes() != null
                                ? detail.getLateMinutes()
                                : 0));

                table.addCell(String.valueOf(
                        detail.getEarlyExitMinutes() != null
                                ? detail.getEarlyExitMinutes()
                                : 0));

                table.addCell(String.valueOf(
                        detail.getOvertimeMinutes() != null
                                ? detail.getOvertimeMinutes()
                                : 0));

                table.addCell(detail.getAttendanceStatus() != null
                        ? detail.getAttendanceStatus().name()
                        : "-");

                String breaks = "-";

                if (detail.getBreaks() != null && !detail.getBreaks().isEmpty()) {

                    breaks = detail.getBreaks()
                            .stream()
                            .map(b -> b.getBreakType()
                                    + " ("
                                    + b.getBreakDurationMinutes()
                                    + " mins)")
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("-");
                }

                table.addCell(breaks);
            }
        }

        return table;
    }
    private static PdfPTable createLeaveDetailTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        PdfPCell title = new PdfPCell(
                new Phrase("Leave Details", headerFont));

        title.setColspan(6);
        title.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(title);

        // Header
        table.addCell(new Phrase("Leave Type", headerFont));
        table.addCell(new Phrase("Start Date", headerFont));
        table.addCell(new Phrase("End Date", headerFont));
        table.addCell(new Phrase("Days", headerFont));
        table.addCell(new Phrase("Status", headerFont));
        table.addCell(new Phrase("Reason", headerFont));

        if (report.getLeaveDetails() != null) {

            for (LeaveDetailResponse detail : report.getLeaveDetails()) {

                table.addCell(
                        detail.getLeaveType() != null
                                ? detail.getLeaveType()
                                : "-");

                table.addCell(
                        detail.getStartDate() != null
                                ? detail.getStartDate().toString()
                                : "-");

                table.addCell(
                        detail.getEndDate() != null
                                ? detail.getEndDate().toString()
                                : "-");

                table.addCell(String.valueOf(
                        detail.getTotalDays() != null
                                ? detail.getTotalDays()
                                : 0));

                table.addCell(
                        detail.getStatus() != null
                                ? detail.getStatus().name()
                                : "-");

                table.addCell(
                        detail.getReason() != null
                                ? detail.getReason()
                                : "-");
            }
        }

        return table;
    }
    private static PdfPTable createEmployeeInfoTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        PdfPCell titleCell = new PdfPCell(
                new Phrase("Employee Information", headerFont));

        titleCell.setColspan(2);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(titleCell);

        addRow(
                table,
                "Employee Code",
                report.getEmployeeCode(),
                headerFont,
                normalFont);

        addRow(
                table,
                "Employee Name",
                report.getEmployeeName(),
                headerFont,
                normalFont);

        addRow(
                table,
                "Department",
                report.getDepartment(),
                headerFont,
                normalFont);

        addRow(
                table,
                "Designation",
                report.getDesignation(),
                headerFont,
                normalFont);

        return table;
    }
    private static PdfPTable createAttendanceSummaryTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        PdfPCell titleCell = new PdfPCell(
                new Phrase("Attendance Summary", headerFont));

        titleCell.setColspan(2);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(titleCell);

        addRow(
                table,
                "Present Days",
                String.valueOf(report.getAttendanceSummary().getPresentDays()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Absent Days",
                String.valueOf(report.getAttendanceSummary().getAbsentDays()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Half Days",
                String.valueOf(report.getAttendanceSummary().getHalfDays()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Late Days",
                String.valueOf(report.getAttendanceSummary().getLateDays()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Working Minutes",
                String.valueOf(report.getAttendanceSummary().getTotalWorkingMinutes()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Break Minutes",
                String.valueOf(report.getAttendanceSummary().getTotalBreakMinutes()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Late Minutes",
                String.valueOf(report.getAttendanceSummary().getTotalLateMinutes()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Early Exit Minutes",
                String.valueOf(report.getAttendanceSummary().getTotalEarlyExitMinutes()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Overtime Minutes",
                String.valueOf(report.getAttendanceSummary().getTotalOvertimeMinutes()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Attendance Percentage",
                String.format("%.2f%%",
                        report.getAttendanceSummary().getAttendancePercentage()),
                headerFont,
                normalFont);

        return table;
    }
    private static PdfPTable createLeaveSummaryTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        PdfPCell titleCell = new PdfPCell(
                new Phrase("Leave Summary", headerFont));

        titleCell.setColspan(2);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(titleCell);

        addRow(
                table,
                "Allocated Leaves",
                String.valueOf(report.getLeaveSummary().getAllocatedLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Used Leaves",
                String.valueOf(report.getLeaveSummary().getUsedLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Remaining Leaves",
                String.valueOf(report.getLeaveSummary().getRemainingLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Expired Leaves",
                String.valueOf(report.getLeaveSummary().getExpiredLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Pending Leaves",
                String.valueOf(report.getLeaveSummary().getPendingLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Approved Leaves",
                String.valueOf(report.getLeaveSummary().getApprovedLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Rejected Leaves",
                String.valueOf(report.getLeaveSummary().getRejectedLeaves()),
                headerFont,
                normalFont);

        addRow(
                table,
                "Cancelled Leaves",
                String.valueOf(report.getLeaveSummary().getCancelledLeaves()),
                headerFont,
                normalFont);

        return table;
    }
    private static void addRow(
            PdfPTable table,
            String label,
            String value,
            Font headerFont,
            Font normalFont) {

        PdfPCell labelCell = new PdfPCell(
                new Phrase(label, headerFont));

        labelCell.setPadding(6);

        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(
                new Phrase(
                        value == null || value.isBlank() ? "-" : value,
                        normalFont));

        valueCell.setPadding(6);

        table.addCell(valueCell);
    }
}