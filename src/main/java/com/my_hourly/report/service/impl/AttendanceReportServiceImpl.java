package com.my_hourly.report.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.report.dto.response.AttendanceReportPageResponse;
import com.my_hourly.report.dto.request.AttendanceReportRequest;
import com.my_hourly.report.dto.response.AttendanceReportResponse;
import com.my_hourly.report.dto.response.AttendanceSummaryResponse;
import com.my_hourly.report.export.AttendanceExcelExporter;
import com.my_hourly.report.export.AttendancePdfExporter;
import com.my_hourly.report.service.AttendanceReportService;
import com.my_hourly.report.specification.AttendanceReportSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceExcelExporter attendanceExcelExporter;
    private final AttendancePdfExporter attendancePdfExporter;

    @Override
    public AttendanceReportPageResponse getAttendanceReport(
            AttendanceReportRequest request) {

        validateRequest(request);

        applyMonthFilter(request);

        Sort sort = Sort.by(
                Sort.Direction.fromString(request.getSortDir()),
                request.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        Specification<Attendance> specification =
                AttendanceReportSpecification.filter(request);

        Page<Attendance> attendancePage =
                attendanceRepository.findAll(specification, pageable);

        List<AttendanceReportResponse> responses = attendancePage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        AttendanceSummaryResponse summary =
                calculateSummary(specification);

        return AttendanceReportPageResponse.builder()
                .content(responses)
                .summary(summary)
                .page(attendancePage.getNumber())
                .size(attendancePage.getSize())
                .totalElements(attendancePage.getTotalElements())
                .totalPages(attendancePage.getTotalPages())
                .first(attendancePage.isFirst())
                .last(attendancePage.isLast())
                .hasNext(attendancePage.hasNext())
                .hasPrevious(attendancePage.hasPrevious())
                .build();
    }

    private void validateRequest(AttendanceReportRequest request) {

        if (request.getMonth() != null &&
                (request.getMonth() < 1 || request.getMonth() > 12)) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        if (request.getStartDate() != null &&
                request.getEndDate() != null &&
                request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
    }

    private void applyMonthFilter(AttendanceReportRequest request) {

        if (request.getMonth() != null && request.getYear() != null) {

            LocalDate firstDay = LocalDate.of(
                    request.getYear(),
                    request.getMonth(),
                    1
            );

            LocalDate lastDay = firstDay.withDayOfMonth(
                    firstDay.lengthOfMonth()
            );

            request.setStartDate(firstDay);
            request.setEndDate(lastDay);
        }
    }

    private AttendanceReportResponse mapToResponse(Attendance attendance) {

        Double workingHours = attendance.getWorkingMinutes() != null 
                ? attendance.getWorkingMinutes() / 60.0 
                : null;

        return AttendanceReportResponse.builder()
                .employeeId(attendance.getEmployee().getId())
                .employeeCode(attendance.getEmployee().getEmployeeCode())
                .employeeName(
                        attendance.getEmployee().getFirstName()
                                + " "
                                + attendance.getEmployee().getLastName()
                )
                .departmentName(
                        attendance.getEmployee()
                                .getDepartment()
                                .getDepartmentName()
                )
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .workingMinutes(attendance.getWorkingMinutes())
                .breakMinutes(attendance.getTotalBreakMinutes())
                .attendanceStatus(attendance.getAttendanceStatus())
                .workingHours(workingHours)
                .build();
    }

    private AttendanceSummaryResponse calculateSummary(
            Specification<Attendance> specification) {

        List<Attendance> attendances =
                attendanceRepository.findAll(specification);

        long total = attendances.size();

        long present = countByStatus(attendances, AttendanceStatus.PRESENT);
        long absent = countByStatus(attendances, AttendanceStatus.ABSENT);
        long late = countByStatus(attendances, AttendanceStatus.LATE);
        long halfDay = countByStatus(attendances, AttendanceStatus.HALF_DAY);
        long leave = countByStatus(attendances, AttendanceStatus.LEAVE);
        long holiday = countByStatus(attendances, AttendanceStatus.HOLIDAY);
        long weekend = countByStatus(attendances, AttendanceStatus.WEEKEND);

        return AttendanceSummaryResponse.builder()
                .totalRecords(total)
                .presentCount(present)
                .absentCount(absent)
                .lateCount(late)
                .halfDayCount(halfDay)
                .leaveCount(leave)
                .holidayCount(holiday)
                .weekendCount(weekend)
                .attendancePercentage(
                        calculateAttendancePercentage(total, present)
                )
                .build();
    }

    private long countByStatus(
            List<Attendance> attendances,
            AttendanceStatus status) {

        return attendances.stream()
                .filter(a -> a.getAttendanceStatus() == status)
                .count();
    }

    private double calculateAttendancePercentage(
            long total,
            long present) {

        if (total == 0) {
            return 0;
        }

        return (present * 100.0) / total;
    }

    @Override
    public byte[] exportAttendanceReport(
            AttendanceReportRequest request) {

        validateRequest(request);

        applyMonthFilter(request);

        Specification<Attendance> specification =
                AttendanceReportSpecification.filter(request);

        List<Attendance> attendances =
                attendanceRepository.findAll(specification);

        List<AttendanceReportResponse> responses =
                attendances.stream()
                        .map(this::mapToResponse)
                        .toList();

        try {
            return attendanceExcelExporter.export(responses);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to generate attendance excel report",
                    e
            );
        }
    }

    @Override
    public byte[] exportAttendancePdf(
            AttendanceReportRequest request) {

        validateRequest(request);

        applyMonthFilter(request);

        Specification<Attendance> specification =
                AttendanceReportSpecification.filter(request);

        List<AttendanceReportResponse> reports =
                attendanceRepository.findAll(specification)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return attendancePdfExporter.export(reports);

    }
}