package com.my_hourly.report.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.my_hourly.report.dto.EmployeeReportResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class PdfReportGenerator {

    public static ByteArrayInputStream generateEmployeeReport(
            List<EmployeeReportResponse> reports) {

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
                        "Employee Attendance & Leave Report",
                        titleFont);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);

                document.add(title);

                document.add(createEmployeeInfoTable(
                        report,
                        headerFont,
                        normalFont));

                document.add(new Paragraph(" "));

                document.add(createAttendanceTable(
                        report,
                        headerFont,
                        normalFont));

                document.add(new Paragraph(" "));

                document.add(createLeaveTable(
                        report,
                        headerFont,
                        normalFont));

                if (i < reports.size() - 1) {
                    document.newPage();
                }
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to generate PDF Report",
                    e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static PdfPTable createEmployeeInfoTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addRow(table, "Employee Code",
                report.getEmployeeCode(), headerFont, normalFont);

        addRow(table, "Employee Name",
                report.getEmployeeName(), headerFont, normalFont);

        addRow(table, "Department",
                report.getDepartment(), headerFont, normalFont);

        addRow(table, "Designation",
                report.getDesignation(), headerFont, normalFont);

        addRow(table, "From Date",
                String.valueOf(report.getFromDate()), headerFont, normalFont);

        addRow(table, "To Date",
                String.valueOf(report.getToDate()), headerFont, normalFont);

        return table;
    }

    private static PdfPTable createAttendanceTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(
                new Phrase("Attendance Summary", headerFont));

        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell);

        addRow(table, "Present Days",
                String.valueOf(report.getPresentDays()),
                headerFont, normalFont);

        addRow(table, "Absent Days",
                String.valueOf(report.getAbsentDays()),
                headerFont, normalFont);

        addRow(table, "Half Days",
                String.valueOf(report.getHalfDays()),
                headerFont, normalFont);

        addRow(table, "Leave Days",
                String.valueOf(report.getLeaveDays()),
                headerFont, normalFont);

        addRow(table, "Late Days",
                String.valueOf(report.getLateDays()),
                headerFont, normalFont);

        addRow(table, "Working Minutes",
                String.valueOf(report.getTotalWorkingMinutes()),
                headerFont, normalFont);

        addRow(table, "Break Minutes",
                String.valueOf(report.getTotalBreakMinutes()),
                headerFont, normalFont);

        addRow(table, "Late Minutes",
                String.valueOf(report.getTotalLateMinutes()),
                headerFont, normalFont);

        addRow(table, "Early Exit Minutes",
                String.valueOf(report.getTotalEarlyExitMinutes()),
                headerFont, normalFont);

        addRow(table, "Overtime Minutes",
                String.valueOf(report.getTotalOvertimeMinutes()),
                headerFont, normalFont);

        addRow(table, "Attendance %",
                String.format("%.2f", report.getAttendancePercentage()),
                headerFont, normalFont);

        return table;
    }

    private static PdfPTable createLeaveTable(
            EmployeeReportResponse report,
            Font headerFont,
            Font normalFont) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(
                new Phrase("Leave Summary", headerFont));

        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell);

        addRow(table, "Allocated Leaves",
                String.valueOf(report.getAllocatedLeaves()),
                headerFont, normalFont);

        addRow(table, "Used Leaves",
                String.valueOf(report.getUsedLeaves()),
                headerFont, normalFont);

        addRow(table, "Remaining Leaves",
                String.valueOf(report.getRemainingLeaves()),
                headerFont, normalFont);

        addRow(table, "Expired Leaves",
                String.valueOf(report.getExpiredLeaves()),
                headerFont, normalFont);

        addRow(table, "Pending Requests",
                String.valueOf(report.getPendingLeaves()),
                headerFont, normalFont);

        addRow(table, "Approved Requests",
                String.valueOf(report.getApprovedLeaves()),
                headerFont, normalFont);

        addRow(table, "Rejected Requests",
                String.valueOf(report.getRejectedLeaves()),
                headerFont, normalFont);

        addRow(table, "Cancelled Requests",
                String.valueOf(report.getCancelledLeaves()),
                headerFont, normalFont);

        return table;
    }

    private static void addRow(
            PdfPTable table,
            String label,
            String value,
            Font headerFont,
            Font normalFont) {

        table.addCell(new Phrase(label, headerFont));
        table.addCell(new Phrase(
                value == null ? "-" : value,
                normalFont));
    }
}