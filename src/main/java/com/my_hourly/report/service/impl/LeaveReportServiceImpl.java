package com.my_hourly.report.service.impl;

import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.report.dto.request.LeaveReportRequest;
import com.my_hourly.report.dto.response.LeaveReportPageResponse;
import com.my_hourly.report.dto.response.LeaveReportResponse;
import com.my_hourly.report.dto.response.LeaveSummaryResponse;
import com.my_hourly.report.export.LeaveExcelExporter;
import com.my_hourly.report.export.LeavePdfExporter;
import com.my_hourly.report.service.LeaveReportService;
import com.my_hourly.report.specification.LeaveReportSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveReportServiceImpl implements LeaveReportService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveExcelExporter leaveExcelExporter;
    private final LeavePdfExporter leavePdfExporter;

    @Override
    public LeaveReportPageResponse getLeaveReport(
            LeaveReportRequest request) {

        validateLeaveRequest(request);

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

        Specification<LeaveRequest> specification =
                LeaveReportSpecification.filter(request);

        Page<LeaveRequest> leavePage =
                leaveRequestRepository.findAll(specification, pageable);

        List<LeaveReportResponse> responses =
                leavePage.getContent()
                        .stream()
                        .map(this::mapToLeaveResponse)
                        .toList();

        LeaveSummaryResponse summary =
                calculateLeaveSummary(specification);

        return LeaveReportPageResponse.builder()
                .content(responses)
                .summary(summary)
                .page(leavePage.getNumber())
                .size(leavePage.getSize())
                .totalElements(leavePage.getTotalElements())
                .totalPages(leavePage.getTotalPages())
                .first(leavePage.isFirst())
                .last(leavePage.isLast())
                .hasNext(leavePage.hasNext())
                .hasPrevious(leavePage.hasPrevious())
                .build();
    }

    private void validateLeaveRequest(LeaveReportRequest request) {

        if (request.getMonth() != null &&
                (request.getMonth() < 1 ||
                        request.getMonth() > 12)) {

            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        if (request.getStartDate() != null &&
                request.getEndDate() != null &&
                request.getStartDate().isAfter(request.getEndDate())) {

            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }
    }

    private void applyMonthFilter(
            LeaveReportRequest request) {

        if (request.getMonth() != null &&
                request.getYear() != null) {

            LocalDate firstDay = LocalDate.of(
                    request.getYear(),
                    request.getMonth(),
                    1
            );

            LocalDate lastDay =
                    firstDay.withDayOfMonth(
                            firstDay.lengthOfMonth());

            request.setStartDate(firstDay);
            request.setEndDate(lastDay);
        }
    }

    private LeaveReportResponse mapToLeaveResponse(
            LeaveRequest leave) {

        return LeaveReportResponse.builder()

                .leaveId(leave.getId())

                .employeeId(
                        leave.getEmployee().getId())

                .employeeCode(
                        leave.getEmployee().getEmployeeCode())

                .employeeName(
                        leave.getEmployee().getFirstName()
                                + " "
                                + leave.getEmployee().getLastName())

                .departmentName(
                        leave.getEmployee()
                                .getDepartment()
                                .getDepartmentName())

                .leaveType(
                        leave.getLeaveType())

                .leaveStatus(
                        leave.getStatus())

                .startDate(
                        leave.getStartDate())

                .endDate(
                        leave.getEndDate())

                .totalDays(
                        leave.getTotalDays())

                .reason(
                        leave.getReason())

                .createdAt(
                        leave.getCreatedAt())

                .updatedAt(
                        leave.getUpdatedAt())

                .build();
    }

    private LeaveSummaryResponse calculateLeaveSummary(
            Specification<LeaveRequest> specification) {

        List<LeaveRequest> leaves =
                leaveRequestRepository.findAll(specification);

        long total = leaves.size();

        long pending = countByStatus(
                leaves,
                LeaveStatus.PENDING);

//        long managerApproved = countByStatus(
//                leaves,
//                LeaveStatus.MANAGER_APPROVED);

//        long hrApproved = countByStatus(
//                leaves,
//                LeaveStatus.HR_APPROVED);

        long approved = countByStatus(
                leaves,
                LeaveStatus.APPROVED);

        long rejected = countByStatus(
                leaves,
                LeaveStatus.REJECTED);

        long cancelled = countByStatus(
                leaves,
                LeaveStatus.CANCELLED);

        long totalDays = leaves.stream()
                .mapToLong(LeaveRequest::getTotalDays)
                .sum();

        double avgDays = total == 0
                ? 0
                : (double) totalDays / total;

        long uniqueEmployees = leaves.stream()
                .map(l -> l.getEmployee().getId())
                .distinct()
                .count();

        return LeaveSummaryResponse.builder()
                .totalLeaves(total)
                //.approvedLeaves(managerApproved + hrApproved)
                .approvedLeaves(approved)
                .pendingLeaves(pending)
                .rejectedLeaves(rejected)
                .cancelledLeaves(cancelled)
                .totalLeaveDays(totalDays)
                .averageLeaveDays(avgDays)
                .uniqueEmployees(uniqueEmployees)
                .build();
    }

    private long countByStatus(
            List<LeaveRequest> leaves,
            LeaveStatus status) {

        return leaves.stream()
                .filter(l -> l.getStatus() == status)
                .count();
    }

    @Override
    public byte[] exportLeaveExcel(
            LeaveReportRequest request) {

        validateLeaveRequest(request);

        applyMonthFilter(request);

        Specification<LeaveRequest> specification =
                LeaveReportSpecification.filter(request);

        List<LeaveReportResponse> reports =
                leaveRequestRepository.findAll(specification)
                        .stream()
                        .map(this::mapToLeaveResponse)
                        .toList();

        try {
            return leaveExcelExporter.export(reports);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to generate leave excel report",
                    e
            );
        }
    }

    @Override
    public byte[] exportLeavePdf(
            LeaveReportRequest request) {

        validateLeaveRequest(request);

        applyMonthFilter(request);

        Specification<LeaveRequest> specification =
                LeaveReportSpecification.filter(request);

        List<LeaveReportResponse> reports =
                leaveRequestRepository.findAll(specification)
                        .stream()
                        .map(this::mapToLeaveResponse)
                        .toList();

        return leavePdfExporter.export(reports);
    }
}
