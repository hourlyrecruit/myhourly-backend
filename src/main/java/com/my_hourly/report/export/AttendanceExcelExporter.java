package com.my_hourly.report.export;

import com.my_hourly.report.dto.response.AttendanceReportResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class AttendanceExcelExporter {

    public byte[] export(List<AttendanceReportResponse> reports)
            throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("Attendance Report");

        int rowNum = 0;

        Row header = sheet.createRow(rowNum++);

        header.createCell(0).setCellValue("Employee Code");
        header.createCell(1).setCellValue("Employee Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(3).setCellValue("Attendance Date");
        header.createCell(4).setCellValue("Check In");
        header.createCell(5).setCellValue("Check Out");
        header.createCell(6).setCellValue("Working Minutes");
        header.createCell(7).setCellValue("Break Minutes");
        header.createCell(8).setCellValue("Status");

        for (AttendanceReportResponse report : reports) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(report.getEmployeeCode());
            row.createCell(1).setCellValue(report.getEmployeeName());
            row.createCell(2).setCellValue(report.getDepartmentName());

            row.createCell(3).setCellValue(
                    report.getAttendanceDate().toString()
            );

            row.createCell(4).setCellValue(
                    report.getCheckInTime() == null
                            ? ""
                            : report.getCheckInTime().toString()
            );

            row.createCell(5).setCellValue(
                    report.getCheckOutTime() == null
                            ? ""
                            : report.getCheckOutTime().toString()
            );

            row.createCell(6).setCellValue(report.getWorkingMinutes());
            row.createCell(7).setCellValue(report.getBreakMinutes());

            row.createCell(8).setCellValue(
                    report.getAttendanceStatus().name()
            );
        }

        for (int i = 0; i <= 8; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        workbook.write(outputStream);

        workbook.close();

        return outputStream.toByteArray();
    }
}
