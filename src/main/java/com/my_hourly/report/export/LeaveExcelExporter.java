package com.my_hourly.report.export;

import com.my_hourly.report.dto.response.LeaveReportResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class LeaveExcelExporter {

    public byte[] export(List<LeaveReportResponse> reports)
            throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("Leave Report");

        int rowNum = 0;

        // Header
        Row header = sheet.createRow(rowNum++);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Leave Type");
        header.createCell(4).setCellValue("Leave Status");
        header.createCell(5).setCellValue("Start Date");
        header.createCell(6).setCellValue("End Date");
        header.createCell(7).setCellValue("Total Days");
        header.createCell(8).setCellValue("Reason");
        header.createCell(9).setCellValue("Created At");
        header.createCell(10).setCellValue("Updated At");
        header.createCell(11).setCellValue("EmployeeId");
        header.createCell(12).setCellValue("Leave Request Id");

        // Data
        for (LeaveReportResponse report : reports) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(report.getEmployeeCode());
            row.createCell(1).setCellValue(report.getEmployeeName());
            row.createCell(2).setCellValue(report.getDepartmentName());

            row.createCell(3).setCellValue(
                    report.getLeaveType() == null
                            ? ""
                            : report.getLeaveType().toString()
            );

            row.createCell(4).setCellValue(
                    report.getLeaveStatus() == null
                            ? ""
                            : report.getLeaveStatus().name()
            );

            row.createCell(5).setCellValue(
                    report.getStartDate() == null
                            ? ""
                            : report.getStartDate().toString()
            );

            row.createCell(6).setCellValue(
                    report.getEndDate() == null
                            ? ""
                            : report.getEndDate().toString()
            );

            row.createCell(7).setCellValue(
                    report.getTotalDays() == null
                            ? 0
                            : report.getTotalDays()
            );

            row.createCell(8).setCellValue(
                    report.getReason() == null
                            ? ""
                            : report.getReason()
            );

            row.createCell(9).setCellValue(
                    report.getCreatedAt() == null
                            ? ""
                            : report.getCreatedAt().toString()
            );

            row.createCell(10).setCellValue(
                    report.getUpdatedAt() == null
                            ? ""
                            : report.getUpdatedAt().toString()
            );

            row.createCell(11).setCellValue(
                    report.getEmployeeId() == null
                        ? ""
                            : report.getEmployeeId().toString()
            );

            row.createCell(12).setCellValue(
                    report.getLeaveId() == null
                        ? ""
                            : report.getLeaveId().toString()
            );
        }

        // Auto Size Columns
        for (int i = 0; i <= 12; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
