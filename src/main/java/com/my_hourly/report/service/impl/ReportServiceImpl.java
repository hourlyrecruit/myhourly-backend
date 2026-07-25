package com.my_hourly.report.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.report.dto.ApiResponse;
import com.my_hourly.report.dto.EmployeeReportResponse;
import com.my_hourly.report.dto.ReportRequest;
import com.my_hourly.report.entity.Report;
import com.my_hourly.report.entity.ReportFormat;
import com.my_hourly.report.repository.ReportRepository;
import com.my_hourly.report.service.ReportService;
import com.my_hourly.report.util.ExcelReportGenerator;
import com.my_hourly.report.util.PdfReportGenerator;
import com.my_hourly.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;

    private final AttendanceRepository attendanceRepository;

    private final LeaveRequestRepository leaveRequestRepository;

    private final LeaveBalanceRepository leaveBalanceRepository;

    private final ReportRepository reportRepository;

    @Override
    public EmployeeReportResponse getEmployeeReport(
            Long employeeId,
            LocalDate fromDate,
            LocalDate toDate) {


        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found.",
                                ErrorCode.RESOURCE_NOT_FOUND));


        EmployeeReportResponse response =
                new EmployeeReportResponse();


        response.setEmployeeId(employee.getId());

        response.setEmployeeCode(
                employee.getEmployeeCode()
        );

        response.setEmployeeName(
                employee.getFirstName()
                        + " "
                        + employee.getLastName()
        );


        response.setDepartment(
                employee.getDepartment() != null ?
                        employee.getDepartment().getDepartmentName()
                        :
                        "-"
        );


        response.setDesignation(
                employee.getDesignation() != null ?
                        employee.getDesignation().getDesignationName()
                        :
                        "-"
        );


        response.setFromDate(fromDate);
        response.setToDate(toDate);


        populateAttendance(
                employee,
                fromDate,
                toDate,
                response
        );


        populateLeave(
                employee,
                fromDate,
                toDate,
                response
        );


        return response;
    }
    public List<EmployeeReportResponse> getEmployeeReports(LocalDate fromDate, LocalDate toDate){
        List<Employee> employees = employeeRepository.findByActiveTrue();
        return employees.stream()
                .map(employee -> getEmployeeReport(
                        employee.getId(),
                        fromDate,
                        toDate))
                .toList();
    }
    private void validateRequest(ReportRequest request) {

        if (request.getFromDate() == null || request.getToDate() == null) {
            throw new BadRequestException(
                    "From date and To date are required.",
                    ErrorCode.VALIDATION_FAILED);
        }

        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new BadRequestException(
                    "From date cannot be after To date.",
                    ErrorCode.VALIDATION_FAILED);
        }

        if (request.getReportType() == null) {
            throw new BadRequestException(
                    "Report type is required.",
                    ErrorCode.VALIDATION_FAILED);
        }

        if (request.getReportFormat() == null) {
            throw new BadRequestException(
                    "Report format is required.",
                    ErrorCode.VALIDATION_FAILED
            );
        }
    }
    private Employee getCurrentEmployee() {
        User user = SecurityUtils.getCurrentUser();
        return employeeRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found.",
                                ErrorCode.RESOURCE_NOT_FOUND));
    }
    private void populateAttendance(
            Employee employee,
            LocalDate fromDate,
            LocalDate toDate,
            EmployeeReportResponse response) {

        List<Attendance> attendances =
                attendanceRepository
                        .findByEmployeeAndAttendanceDateBetween(
                                employee,
                                fromDate,
                                toDate);

        response.setPresentDays(
                attendances.stream()
                        .filter(a -> a.getAttendanceStatus() ==
                                AttendanceStatus.PRESENT)
                        .count());

        response.setAbsentDays(
                attendances.stream()
                        .filter(a -> a.getAttendanceStatus() ==
                                AttendanceStatus.ABSENT)
                        .count());

        response.setHalfDays(
                attendances.stream()
                        .filter(a -> a.getAttendanceStatus() ==
                                AttendanceStatus.HALF_DAY)
                        .count());

        response.setLeaveDays(
                attendances.stream()
                        .filter(a -> a.getAttendanceStatus() ==
                                AttendanceStatus.LEAVE)
                        .count());

        response.setLateDays(
                attendances.stream()
                        .filter(a -> a.getLateMinutes() != null
                                && a.getLateMinutes() > 0)
                        .count());

        response.setTotalWorkingMinutes(
                attendances.stream()
                        .mapToInt(a ->
                                a.getWorkingMinutes() == null
                                        ? 0
                                        : a.getWorkingMinutes())
                        .sum());

        response.setTotalBreakMinutes(
                attendances.stream()
                        .mapToInt(a ->
                                a.getTotalBreakMinutes() == null
                                        ? 0
                                        : a.getTotalBreakMinutes())
                        .sum());

        response.setTotalLateMinutes(
                attendances.stream()
                        .mapToInt(a ->
                                a.getLateMinutes() == null
                                        ? 0
                                        : a.getLateMinutes())
                        .sum());

        response.setTotalEarlyExitMinutes(
                attendances.stream()
                        .mapToInt(a ->
                                a.getEarlyExitMinutes() == null
                                        ? 0
                                        : a.getEarlyExitMinutes())
                        .sum());

        response.setTotalOvertimeMinutes(
                attendances.stream()
                        .mapToInt(a ->
                                a.getOvertimeMinutes() == null
                                        ? 0
                                        : a.getOvertimeMinutes())
                        .sum());

        long workingDays =
                response.getPresentDays()
                        + response.getAbsentDays()
                        + response.getHalfDays()
                        + response.getLeaveDays();

        double percentage =
                workingDays == 0
                        ? 0
                        : (response.getPresentDays() * 100.0) / workingDays;

        response.setAttendancePercentage(percentage);
    }
    private void populateLeave(
            Employee employee,
            LocalDate fromDate,
            LocalDate toDate,
            EmployeeReportResponse response) {

        List<LeaveBalance> balances =
                leaveBalanceRepository.findByEmployeeAndYear(
                        employee,
                        fromDate.getYear());

        response.setAllocatedLeaves(
                balances.stream()
                        .mapToInt(LeaveBalance::getAllocatedLeaves)
                        .sum());

        response.setUsedLeaves(
                balances.stream()
                        .mapToInt(LeaveBalance::getUsedLeaves)
                        .sum());

        response.setRemainingLeaves(
                balances.stream()
                        .mapToInt(LeaveBalance::getRemainingLeaves)
                        .sum());

        response.setExpiredLeaves(
                balances.stream()
                        .mapToInt(LeaveBalance::getExpiredLeaves)
                        .sum());

        List<LeaveRequest> requests =
                leaveRequestRepository.findEmployeeLeavesBetween(
                        employee,
                        fromDate,
                        toDate);

        response.setPendingLeaves(
                requests.stream()
                        .filter(r -> r.getStatus() ==
                                LeaveStatus.PENDING)
                        .count());

        response.setApprovedLeaves(
                requests.stream()
                        .filter(r ->
                                r.getStatus() == LeaveStatus.HR_APPROVED)
                        .count());

        response.setRejectedLeaves(
                requests.stream()
                        .filter(r ->
                                r.getStatus() == LeaveStatus.REJECTED)
                        .count());

        response.setCancelledLeaves(
                requests.stream()
                        .filter(r ->
                                r.getStatus() == LeaveStatus.CANCELLED)
                        .count());
    }
    private List<EmployeeReportResponse> prepareEmployeeReports(
            ReportRequest request) {

        List<Employee> employees;

        if (request.getEmployeeIds() == null ||
                request.getEmployeeIds().isEmpty()) {

            employees = employeeRepository.findByActiveTrue();

        } else {

            employees = employeeRepository.findAllById(
                    request.getEmployeeIds());

        }

        return employees.stream()
                .map(employee -> {

                    EmployeeReportResponse response =
                            new EmployeeReportResponse();

                    response.setEmployeeId(employee.getId());
                    response.setEmployeeCode(employee.getEmployeeCode());
                    response.setEmployeeName(
                            employee.getFirstName() + " " +
                                    employee.getLastName());

                    response.setDepartment(
                            employee.getDepartment() != null
                                    ? employee.getDepartment().getDepartmentName()
                                    : "-");

                    response.setDesignation(
                            employee.getDesignation() != null
                                    ? employee.getDesignation().getDesignationName()
                                    : "-");

                    response.setFromDate(request.getFromDate());
                    response.setToDate(request.getToDate());

                    switch (request.getReportType()) {

                        case ATTENDANCE ->
                                populateAttendance(
                                        employee,
                                        request.getFromDate(),
                                        request.getToDate(),
                                        response);

                        case LEAVE ->
                                populateLeave(
                                        employee,
                                        request.getFromDate(),
                                        request.getToDate(),
                                        response);

                        case LEAVE_ATTENDANCE -> {

                            populateAttendance(
                                    employee,
                                    request.getFromDate(),
                                    request.getToDate(),
                                    response);

                            populateLeave(
                                    employee,
                                    request.getFromDate(),
                                    request.getToDate(),
                                    response);
                        }
                    }

                    return response;

                })
                .toList();
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse> generatePdfReport(
            ReportRequest request) {

        validateRequest(request);

        List<EmployeeReportResponse> reports =
                prepareEmployeeReports(request);

        ByteArrayInputStream pdf =
                PdfReportGenerator.generateEmployeeReport(reports);

        String fileName =
                "Employee_Report_" + System.currentTimeMillis() + ".pdf";

        try {

            Path path = Paths.get("reports", fileName);

            Files.createDirectories(path.getParent());

            Files.copy(
                    pdf,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Report report = Report.builder()
                    .reportType(request.getReportType())
                    .reportFormat(ReportFormat.PDF)
                    .generatedBy(getCurrentEmployee())
                    .fromDate(request.getFromDate())
                    .toDate(request.getToDate())
                    .fileName(fileName)
                    .filePath(path.toString())
                    .fileSize(Files.size(path))
                    .build();

            reportRepository.save(report);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report.", e);
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "PDF report generated successfully."
                )
        );
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse> generateExcelReport(
            ReportRequest request) {

        validateRequest(request);

        List<EmployeeReportResponse> reports =
                prepareEmployeeReports(request);

        ByteArrayInputStream excel =
                ExcelReportGenerator.generateEmployeeReport(reports);

        String fileName =
                "Employee_Report_" + System.currentTimeMillis() + ".xlsx";

        try {

            Path path = Paths.get("reports", fileName);

            Files.createDirectories(path.getParent());

            Files.copy(
                    excel,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );

            long fileSize = Files.size(path);

            Report report = Report.builder()
                    .reportType(request.getReportType())
                    .reportFormat(ReportFormat.EXCEL)
                    .generatedBy(getCurrentEmployee())
                    .fromDate(request.getFromDate())
                    .toDate(request.getToDate())
                    .fileName(fileName)
                    .filePath(path.toString())
                    .fileSize(fileSize)
                    .build();

            reportRepository.save(report);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report.", e);
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Excel report generated successfully."
                )
        );
    }

}
