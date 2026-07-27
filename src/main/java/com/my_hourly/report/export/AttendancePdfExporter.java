package com.my_hourly.report.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.my_hourly.report.dto.response.AttendanceReportResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class AttendancePdfExporter {

    public byte[] export(List<AttendanceReportResponse> reports) {

        try {

            Document document = new Document(PageSize.A4.rotate());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);

            Paragraph title = new Paragraph("Attendance Report", titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(9);

            table.setWidthPercentage(100);

            table.setWidths(new float[]{
                    2f,
                    3f,
                    3f,
                    2f,
                    3f,
                    3f,
                    2f,
                    2f,
                    2f
            });

            addHeader(table);

            for (AttendanceReportResponse report : reports) {

                table.addCell(report.getEmployeeCode());

                table.addCell(report.getEmployeeName());

                table.addCell(report.getDepartmentName());

                table.addCell(report.getAttendanceDate().toString());

                table.addCell(report.getCheckInTime() == null ?
                        "" :
                        report.getCheckInTime().toString());

                table.addCell(report.getCheckOutTime() == null ?
                        "" :
                        report.getCheckOutTime().toString());

                table.addCell(String.valueOf(report.getWorkingMinutes()));

                table.addCell(String.valueOf(report.getBreakMinutes()));

                table.addCell(report.getAttendanceStatus().name());
            }

            document.add(table);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException("Unable to generate PDF", e);
        }
    }

    private void addHeader(PdfPTable table) {

        addCell(table, "Employee Code");
        addCell(table, "Employee Name");
        addCell(table, "Department");
        addCell(table, "Attendance Date");
        addCell(table, "Check In");
        addCell(table, "Check Out");
        addCell(table, "Working Minutes");
        addCell(table, "Break Minutes");
        addCell(table, "Status");
    }

    private void addCell(PdfPTable table, String value) {

        PdfPCell cell = new PdfPCell(new Phrase(value));

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell);
    }
}
