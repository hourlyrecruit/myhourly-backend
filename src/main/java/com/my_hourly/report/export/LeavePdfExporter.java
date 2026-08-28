package com.my_hourly.report.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.my_hourly.report.dto.response.LeaveReportResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class LeavePdfExporter {

    private final Font titleFont = new Font(
            Font.HELVETICA,
            18,
            Font.BOLD
    );

    // Header font
    private final Font headerFont = new Font(
            Font.HELVETICA,
            8,
            Font.BOLD
    );

    // Table content font
    private final Font tableFont = new Font(
            Font.HELVETICA,
            7,
            Font.NORMAL
    );

    public byte[] export(List<LeaveReportResponse> reports) {

        try {

            Document document =
                    new Document(PageSize.A4.rotate());

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            // =========================
            // Title
            // =========================

            Paragraph title =
                    new Paragraph(
                            "Leave Report",
                            titleFont
                    );

            title.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(title);

            document.add(
                    new Paragraph(" ")
            );

            // =========================
            // Table
            // =========================

            PdfPTable table =
                    getPdfPTable();

            addHeader(table);

            for (LeaveReportResponse report : reports) {

                // Employee Code
                addDataCell(
                        table,
                        value(report.getEmployeeCode())
                );

                // Employee Id
                addDataCell(
                        table,
                        report.getEmployeeId() == null
                                ? ""
                                : report.getEmployeeId().toString()
                );

                // Employee Name
                addDataCell(
                        table,
                        value(report.getEmployeeName())
                );

                // Department
                addDataCell(
                        table,
                        value(report.getDepartmentName())
                );

                // Leave Type
                addDataCell(
                        table,
                        report.getLeaveType() == null
                                ? ""
                                : report.getLeaveType().toString()
                );

                // Leave Id
                addDataCell(
                        table,
                        report.getLeaveId() == null
                                ? ""
                                : report.getLeaveId().toString()
                );

                // Status
                addDataCell(
                        table,
                        report.getLeaveStatus() == null
                                ? ""
                                : report.getLeaveStatus().name()
                );

                // Start Date
                addDataCell(
                        table,
                        report.getStartDate() == null
                                ? ""
                                : report.getStartDate().toString()
                );

                // End Date
                addDataCell(
                        table,
                        report.getEndDate() == null
                                ? ""
                                : report.getEndDate().toString()
                );

                // Total Days
                addDataCell(
                        table,
                        String.valueOf(
                                report.getTotalDays()
                        )
                );

                // Reason
                addDataCell(
                        table,
                        value(report.getReason())
                );

                // Created At
                addDataCell(
                        table,
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

    // =========================
    // Create PDF Table
    // =========================

    private static @NonNull PdfPTable getPdfPTable() {

        PdfPTable table =
                new PdfPTable(12);

        table.setWidthPercentage(100);

        table.setWidths(
                new float[]{
                        2f,    // Employee Code
                        1.5f,  // Employee Id
                        3f,    // Employee Name
                        2f,    // Department
                        2f,    // Leave Type
                        1.5f,  // Leave Id
                        2f,    // Status
                        2f,    // Start Date
                        2f,    // End Date
                        1.5f,  // Days
                        4f,    // Reason
                        3f     // Created At
                }
        );

        return table;
    }

    // =========================
    // Table Header
    // =========================

    private void addHeader(
            PdfPTable table) {

        addHeaderCell(
                table,
                "Employee Code"
        );

        addHeaderCell(
                table,
                "Employee Id"
        );

        addHeaderCell(
                table,
                "Employee Name"
        );

        addHeaderCell(
                table,
                "Department"
        );

        addHeaderCell(
                table,
                "Leave Type"
        );

        addHeaderCell(
                table,
                "Leave Id"
        );

        addHeaderCell(
                table,
                "Status"
        );

        addHeaderCell(
                table,
                "Start Date"
        );

        addHeaderCell(
                table,
                "End Date"
        );

        addHeaderCell(
                table,
                "Days"
        );

        addHeaderCell(
                table,
                "Reason"
        );

        addHeaderCell(
                table,
                "Created At"
        );
    }

    // =========================
    // Header Cell
    // =========================

    private void addHeaderCell(
            PdfPTable table,
            String value) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                value,
                                headerFont
                        )
                );

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        // Reduce header padding
        cell.setPadding(2);

        table.addCell(cell);
    }

    // =========================
    // Data Cell
    // =========================

    private void addDataCell(
            PdfPTable table,
            String value) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                value,
                                tableFont
                        )
                );

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        // Reduce cell padding
        cell.setPadding(2);

        table.addCell(cell);
    }

    // =========================
    // Null Handling
    // =========================

    private String value(String value) {

        return value == null
                ? ""
                : value;
    }
}