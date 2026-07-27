package com.my_hourly.report.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.my_hourly.report.dto.response.LeaveReportResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class LeavePdfExporter {

    public byte[] export(List<LeaveReportResponse> reports) {

        try {

            Document document = new Document(PageSize.A4.rotate());

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(
                    Font.HELVETICA,
                    18,
                    Font.BOLD
            );

            Paragraph title =
                    new Paragraph("Leave Report", titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(10);

            table.setWidthPercentage(100);

            table.setWidths(new float[]{
                    2f, // Employee Code
                    3f, // Employee Name
                    3f, // Department
                    2f, // Leave Type
                    2f, // Status
                    2f, // Start
                    2f, // End
                    1.5f, // Days
                    4f, // Reason
                    3f  // Created At
            });

            addHeader(table);

            for (LeaveReportResponse report : reports) {

                table.addCell(value(report.getEmployeeCode()));
                table.addCell(value(report.getEmployeeName()));
                table.addCell(value(report.getDepartmentName()));

                table.addCell(
                        report.getLeaveType() == null
                                ? ""
                                : report.getLeaveType().toString()
                );

                table.addCell(
                        report.getLeaveStatus() == null
                                ? ""
                                : report.getLeaveStatus().name()
                );

                table.addCell(
                        report.getStartDate() == null
                                ? ""
                                : report.getStartDate().toString()
                );

                table.addCell(
                        report.getEndDate() == null
                                ? ""
                                : report.getEndDate().toString()
                );

                table.addCell(
                        String.valueOf(report.getTotalDays())
                );

                table.addCell(value(report.getReason()));

                table.addCell(
                        report.getCreatedAt() == null
                                ? ""
                                : report.getCreatedAt().toString()
                );
            }

            document.add(table);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate Leave PDF",
                    e
            );
        }
    }

    private void addHeader(PdfPTable table) {

        addCell(table, "Employee Code");
        addCell(table, "Employee Name");
        addCell(table, "Department");
        addCell(table, "Leave Type");
        addCell(table, "Status");
        addCell(table, "Start Date");
        addCell(table, "End Date");
        addCell(table, "Days");
        addCell(table, "Reason");
        addCell(table, "Created At");
    }

    private void addCell(PdfPTable table, String value) {

        PdfPCell cell = new PdfPCell(new Phrase(value));

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell);
    }

    private String value(String value) {

        return value == null ? "" : value;
    }
}