package com.my_hourly.report.util;

import com.my_hourly.report.dto.EmployeeReportResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportGenerator {

    public static ByteArrayInputStream generateEmployeeReport(
            List<EmployeeReportResponse> reports) {

        try (
                XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.createSheet("Employee Report");

            int rowNumber = 0;

            Row header = sheet.createRow(rowNumber++);

            header.createCell(0).setCellValue("Employee Code");
            header.createCell(1).setCellValue("Employee Name");
            header.createCell(2).setCellValue("Department");
            header.createCell(3).setCellValue("Designation");

            header.createCell(4).setCellValue("Present");
            header.createCell(5).setCellValue("Absent");
            header.createCell(6).setCellValue("Half Day");
            header.createCell(7).setCellValue("Leave");
            header.createCell(8).setCellValue("Late");

            header.createCell(9).setCellValue("Working Minutes");
            header.createCell(10).setCellValue("Break Minutes");
            header.createCell(11).setCellValue("Late Minutes");
            header.createCell(12).setCellValue("Early Exit");
            header.createCell(13).setCellValue("Overtime");

            header.createCell(14).setCellValue("Attendance %");

            header.createCell(15).setCellValue("Allocated Leaves");
            header.createCell(16).setCellValue("Used Leaves");
            header.createCell(17).setCellValue("Remaining Leaves");
            header.createCell(18).setCellValue("Expired Leaves");

            header.createCell(19).setCellValue("Pending");
            header.createCell(20).setCellValue("Approved");
            header.createCell(21).setCellValue("Rejected");
            header.createCell(22).setCellValue("Cancelled");

            for (EmployeeReportResponse report : reports) {

                Row row = sheet.createRow(rowNumber++);

                row.createCell(0).setCellValue(report.getEmployeeCode());
                row.createCell(1).setCellValue(report.getEmployeeName());
                row.createCell(2).setCellValue(report.getDepartment());
                row.createCell(3).setCellValue(report.getDesignation());

                row.createCell(4).setCellValue(report.getPresentDays());
                row.createCell(5).setCellValue(report.getAbsentDays());
                row.createCell(6).setCellValue(report.getHalfDays());
                row.createCell(7).setCellValue(report.getLeaveDays());
                row.createCell(8).setCellValue(report.getLateDays());

                row.createCell(9).setCellValue(report.getTotalWorkingMinutes());
                row.createCell(10).setCellValue(report.getTotalBreakMinutes());
                row.createCell(11).setCellValue(report.getTotalLateMinutes());
                row.createCell(12).setCellValue(report.getTotalEarlyExitMinutes());
                row.createCell(13).setCellValue(report.getTotalOvertimeMinutes());

                row.createCell(14).setCellValue(report.getAttendancePercentage());

                row.createCell(15).setCellValue(report.getAllocatedLeaves());
                row.createCell(16).setCellValue(report.getUsedLeaves());
                row.createCell(17).setCellValue(report.getRemainingLeaves());
                row.createCell(18).setCellValue(report.getExpiredLeaves());

                row.createCell(19).setCellValue(report.getPendingLeaves());
                row.createCell(20).setCellValue(report.getApprovedLeaves());
                row.createCell(21).setCellValue(report.getRejectedLeaves());
                row.createCell(22).setCellValue(report.getCancelledLeaves());
            }

            for (int i = 0; i < 23; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate Excel report.", ex);
        }
    }

}