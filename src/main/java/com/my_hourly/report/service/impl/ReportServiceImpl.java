package com.my_hourly.report.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.report.dto.*;
import com.my_hourly.report.entity.ReportType;
import com.my_hourly.report.service.ReportService;
import com.my_hourly.report.util.ExcelReportGenerator;
import com.my_hourly.report.util.PdfReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;

    private final AttendanceRepository attendanceRepository;

    private final LeaveRequestRepository leaveRequestRepository;

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public Page<EmployeeReportResponse> getReports(
            AttendanceReportFilter filter) {

        String sortBy =
                filter.getSortBy() != null
                        ? filter.getSortBy()
                        : "firstName";


        String sortDirection =
                filter.getSortDirection() != null
                        ? filter.getSortDirection()
                        : "ASC";


        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy
        );

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort);

        Page<Employee> employees;

        // Employee Id Filter
        if (filter.getEmployeeId() != null) {

            Employee employee = employeeRepository.findById(
                            filter.getEmployeeId())
                    .orElseThrow(() ->
                            new RuntimeException("Employee not found."));

            EmployeeReportResponse response =
                    mapEmployee(employee, filter);

            return new PageImpl<>(
                    List.of(response),
                    pageable,
                    1);

        }

        // Department Filter
        else if (filter.getDepartmentId() != null) {

            employees =
                    employeeRepository.findByDepartmentId(
                            filter.getDepartmentId(),
                            pageable);

        }

        // Employee Name Filter
        else if (filter.getEmployeeName() != null
                && !filter.getEmployeeName().isBlank()) {

            employees =
                    employeeRepository
                            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                                    filter.getEmployeeName(),
                                    filter.getEmployeeName(),
                                    pageable);

        }

        // All Employees
        else {

            employees =
                    employeeRepository.findByActiveTrue(pageable);

        }

        return employees.map(employee ->
                mapEmployee(employee, filter));
    }
    @Override
    public EmployeeReportResponse getEmployeeReport(
            Long employeeId,
            AttendanceReportFilter filter) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return mapEmployee(employee, filter);
    }
    private EmployeeReportResponse mapEmployee(
            Employee employee,
            AttendanceReportFilter filter) {

        EmployeeReportResponse response = new EmployeeReportResponse();

        // Employee Details
        response.setEmployeeId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setEmployeeName(
                employee.getFirstName() + " " + employee.getLastName());

        response.setDepartment(
                employee.getDepartment() != null
                        ? employee.getDepartment().getDepartmentName()
                        : "-");

        response.setDesignation(
                employee.getDesignation() != null
                        ? employee.getDesignation().getDesignationName()
                        : "-");

        switch (filter.getReportType()) {

            case ATTENDANCE -> {
                response.setAttendanceSummary(
                        populateAttendanceSummary(employee, filter));
            }

            case LEAVE -> {
                response.setLeaveSummary(
                        populateLeaveSummary(employee, filter));
            }

            case ATTENDANCE_LEAVE -> {
                response.setAttendanceSummary(
                        populateAttendanceSummary(employee, filter));

                response.setLeaveSummary(
                        populateLeaveSummary(employee, filter));
            }

            case ATTENDANCE_DETAIL -> {
                response.setAttendanceDetails(
                        populateAttendanceDetails(employee, filter));
            }

            case LEAVE_DETAIL -> {
                response.setLeaveDetails(
                        populateLeaveDetails(employee, filter));
            }

            case ATTENDANCE_LEAVE_DETAIL -> {

                response.setAttendanceDetails(
                        populateAttendanceDetails(employee, filter));

                response.setLeaveDetails(
                        populateLeaveDetails(employee, filter));
            }
        }

        return response;
    }
    private List<AttendanceDetailResponse> populateAttendanceDetails(
            Employee employee,
            AttendanceReportFilter filter) {

        return getFilteredAttendances(employee, filter)
                .stream()
                .map(attendance -> {

                    AttendanceDetailResponse dto =
                            new AttendanceDetailResponse();

                    dto.setAttendanceDate(
                            attendance.getAttendanceDate());

                    dto.setCheckInTime(
                            attendance.getCheckInTime() != null
                                    ? attendance.getCheckInTime().toLocalTime()
                                    : null);

                    dto.setCheckOutTime(
                            attendance.getCheckOutTime() != null
                                    ? attendance.getCheckOutTime().toLocalTime()
                                    : null);

                    dto.setWorkingMinutes(
                            attendance.getWorkingMinutes());

                    dto.setTotalBreakMinutes(
                            attendance.getTotalBreakMinutes());

                    dto.setLateMinutes(
                            attendance.getLateMinutes());

                    dto.setEarlyExitMinutes(
                            attendance.getEarlyExitMinutes());

                    dto.setOvertimeMinutes(
                            attendance.getOvertimeMinutes());

                    dto.setAttendanceStatus(
                            attendance.getAttendanceStatus());

                    dto.setBreaks(
                            attendance.getBreaks()
                                    .stream()
                                    .map(breakEntity -> {

                                        AttendanceBreakResponse breakDto =
                                                new AttendanceBreakResponse();

                                        breakDto.setBreakStartTime(
                                                breakEntity.getBreakStartTime() != null
                                                        ? breakEntity.getBreakStartTime().toLocalTime()
                                                        : null);

                                        breakDto.setBreakEndTime(
                                                breakEntity.getBreakEndTime() != null
                                                        ? breakEntity.getBreakEndTime().toLocalTime()
                                                        : null);

                                        breakDto.setBreakDurationMinutes(
                                                breakEntity.getBreakMinutes());

                                        breakDto.setBreakType(
                                                breakEntity.getBreakType() != null
                                                        ? breakEntity.getBreakType().name()
                                                        : null);

                                        return breakDto;

                                    })
                                    .toList());

                    return dto;

                })
                .toList();
    }
    private List<LeaveDetailResponse> populateLeaveDetails(
            Employee employee,
            AttendanceReportFilter filter) {

        return getFilteredLeaveRequests(employee, filter)
                .stream()
                .map(leave -> {

                    LeaveDetailResponse dto = new LeaveDetailResponse();

                    dto.setLeaveType(
                            leave.getLeaveType().getName());

                    dto.setStartDate(leave.getStartDate());

                    dto.setEndDate(leave.getEndDate());

                    dto.setTotalDays(leave.getTotalDays());

                    dto.setStatus(leave.getStatus());

                    dto.setReason(leave.getReason());

                    return dto;

                })
                .toList();
    }
    private AttendanceReportResponse populateAttendanceSummary(
            Employee employee,
            AttendanceReportFilter filter) {


        List<Attendance> attendances =
                getFilteredAttendances(employee, filter);


        AttendanceReportResponse response =
                new AttendanceReportResponse();



        response.setPresentDays(
                attendances.stream()
                        .filter(a ->
                                a.getAttendanceStatus() == AttendanceStatus.PRESENT)
                        .count());


        response.setAbsentDays(
                attendances.stream()
                        .filter(a ->
                                a.getAttendanceStatus() == AttendanceStatus.ABSENT)
                        .count());


        response.setHalfDays(
                attendances.stream()
                        .filter(a ->
                                a.getAttendanceStatus() == AttendanceStatus.HALF_DAY)
                        .count());


        response.setLateDays(
                attendances.stream()
                        .filter(a ->
                                a.getLateMinutes() != null
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
                        + response.getHalfDays();



        double percentage =
                workingDays == 0
                        ? 0.0
                        : (response.getPresentDays() * 100.0)
                          / workingDays;



        response.setAttendancePercentage(percentage);



        if(filter.getStatus()!=null){
            response.setStatus(filter.getStatus());
        }


        return response;
    }
    private List<Attendance> getFilteredAttendances(
            Employee employee,
            AttendanceReportFilter filter) {

        List<Attendance> attendances;

        // Custom Date Filter
        if (filter.getStartDate() != null && filter.getEndDate() != null) {

            attendances = attendanceRepository
                    .findByEmployeeAndAttendanceDateBetween(
                            employee,
                            filter.getStartDate(),
                            filter.getEndDate());

        }

        // Month Filter
        else if (filter.getMonth() != null && filter.getYear() != null) {

            LocalDate startDate = LocalDate.of(
                    filter.getYear(),
                    filter.getMonth(),
                    1);

            LocalDate endDate = startDate.withDayOfMonth(
                    startDate.lengthOfMonth());

            attendances = attendanceRepository
                    .findByEmployeeAndAttendanceDateBetween(
                            employee,
                            startDate,
                            endDate);

        }

        // Year Filter
        else if (filter.getYear() != null) {

            LocalDate startDate = LocalDate.of(
                    filter.getYear(),
                    1,
                    1);

            LocalDate endDate = LocalDate.of(
                    filter.getYear(),
                    12,
                    31);

            attendances = attendanceRepository
                    .findByEmployeeAndAttendanceDateBetween(
                            employee,
                            startDate,
                            endDate);

        }

        // No Date Filter
        else {

            attendances = attendanceRepository
                    .findByEmployee(employee);

        }

        // Attendance Status Filter

        if(filter.getStatus()!=null){

            attendances =
                    attendances.stream()
                            .filter(attendance ->
                                    attendance.getAttendanceStatus()
                                            .equals(filter.getStatus())
                            )
                            .toList();

        }

        return attendances;
    }
    private LeaveReportResponse populateLeaveSummary(
            Employee employee,
            AttendanceReportFilter filter) {

        List<LeaveBalance> leaveBalances =
                leaveBalanceRepository.findByEmployeeAndYear(
                        employee,
                        filter.getYear() != null
                                ? filter.getYear()
                                : LocalDate.now().getYear());


        List<LeaveRequest> leaveRequests =
                getFilteredLeaveRequests(employee, filter);


        LeaveReportResponse response = new LeaveReportResponse();


        // Leave Balance Summary
        response.setAllocatedLeaves(
                leaveBalances.stream()
                        .mapToInt(LeaveBalance::getAllocatedLeaves)
                        .sum());


        response.setUsedLeaves(
                leaveBalances.stream()
                        .mapToInt(LeaveBalance::getUsedLeaves)
                        .sum());


        response.setRemainingLeaves(
                leaveBalances.stream()
                        .mapToInt(LeaveBalance::getRemainingLeaves)
                        .sum());


        response.setExpiredLeaves(
                leaveBalances.stream()
                        .mapToInt(LeaveBalance::getExpiredLeaves)
                        .sum());


        // Leave Request Summary
        response.setPendingLeaves(
                leaveRequests.stream()
                        .filter(request ->
                                request.getStatus() == LeaveStatus.PENDING)
                        .count());


        response.setApprovedLeaves(
                leaveRequests.stream()
                        .filter(request ->
                                request.getStatus() == LeaveStatus.HR_APPROVED)
                        .count());


        response.setRejectedLeaves(
                leaveRequests.stream()
                        .filter(request ->
                                request.getStatus() == LeaveStatus.REJECTED)
                        .count());


        response.setCancelledLeaves(
                leaveRequests.stream()
                        .filter(request ->
                                request.getStatus() == LeaveStatus.CANCELLED)
                        .count());


        return response;
    }
    private List<LeaveRequest> getFilteredLeaveRequests(
            Employee employee,
            AttendanceReportFilter filter) {

        List<LeaveRequest> leaveRequests;

        // Custom Date Filter
        if (filter.getStartDate() != null
                && filter.getEndDate() != null) {

            leaveRequests =
                    leaveRequestRepository
                            .findEmployeeLeavesBetween(
                                    employee,
                                    filter.getStartDate(),
                                    filter.getEndDate());
        }

        // Month Filter
        else if (filter.getMonth() != null
                && filter.getYear() != null) {

            LocalDate startDate = LocalDate.of(
                    filter.getYear(),
                    filter.getMonth(),
                    1);

            LocalDate endDate = startDate.withDayOfMonth(
                    startDate.lengthOfMonth());


            leaveRequests =
                    leaveRequestRepository
                            .findEmployeeLeavesBetween(
                                    employee,
                                    startDate,
                                    endDate);
        }

        // Year Filter
        else if (filter.getYear() != null) {

            LocalDate startDate = LocalDate.of(
                    filter.getYear(),
                    1,
                    1);

            LocalDate endDate = LocalDate.of(
                    filter.getYear(),
                    12,
                    31);


            leaveRequests =
                    leaveRequestRepository
                            .findEmployeeLeavesBetween(
                                    employee,
                                    startDate,
                                    endDate);
        }

        // No Date Filter
        else {

            leaveRequests =
                    leaveRequestRepository
                            .findByEmployee(employee);
        }


        return leaveRequests;
    }
    @Override
    public ResponseEntity<Resource> generatePdfReport(
            AttendanceReportFilter filter) {


        List<EmployeeReportResponse> reports =
                getReports(filter)
                        .getContent();


        ByteArrayInputStream pdf =
                PdfReportGenerator.generateReport(
                        reports,
                        filter.getReportType());


        ByteArrayResource resource =
                new ByteArrayResource(
                        pdf.readAllBytes()
                );


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=employee-report.pdf")
                .contentType(
                        MediaType.APPLICATION_PDF)
                .contentLength(
                        resource.contentLength())
                .body(resource);
    }
    @Override
    public ResponseEntity<Resource> generateExcelReport(
            AttendanceReportFilter filter) {


        List<EmployeeReportResponse> reports =
                getReports(filter)
                        .getContent();


        ByteArrayInputStream excel =
                ExcelReportGenerator.generateReport(
                        reports,
                        filter.getReportType());


        ByteArrayResource resource =
                new ByteArrayResource(
                        excel.readAllBytes()
                );


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=employee-report.xlsx")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(
                        resource.contentLength())
                .body(resource);
    }
}