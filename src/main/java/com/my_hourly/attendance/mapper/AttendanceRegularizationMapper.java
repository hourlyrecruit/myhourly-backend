package com.my_hourly.attendance.mapper;

import com.my_hourly.attendance.api.response.RegularizationDetailResponse;
import com.my_hourly.attendance.api.response.RegularizationResponse;
import com.my_hourly.attendance.entity.AttendanceRegularization;
import com.my_hourly.attendance.entity.AttendanceRegularizationDetail;
import com.my_hourly.attendance.util.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttendanceRegularizationMapper {

    public RegularizationResponse toResponse(AttendanceRegularization regularization) {
        String employeeName = null;
        Long employeeId = null;
        if (regularization.getEmployee() != null) {
            employeeId = regularization.getEmployee().getId();
            employeeName = regularization.getEmployee().getFirstName();
            if (regularization.getEmployee().getLastName() != null
                    && !regularization.getEmployee().getLastName().isBlank()) {
                employeeName = (employeeName != null ? employeeName : "")
                        + " " + regularization.getEmployee().getLastName();
            }
        }

        String approvedByName = null;
        Long approvedById = null;
        if (regularization.getApprovedBy() != null) {
            approvedById = regularization.getApprovedBy().getId();
            approvedByName = regularization.getApprovedBy().getFirstName();
            if (regularization.getApprovedBy().getLastName() != null
                    && !regularization.getApprovedBy().getLastName().isBlank()) {
                approvedByName = (approvedByName != null ? approvedByName : "")
                        + " " + regularization.getApprovedBy().getLastName();
            }
        }

        String rejectedByName = null;
        Long rejectedById = null;
        if (regularization.getRejectedBy() != null) {
            rejectedById = regularization.getRejectedBy().getId();
            rejectedByName = regularization.getRejectedBy().getFirstName();
            if (regularization.getRejectedBy().getLastName() != null
                    && !regularization.getRejectedBy().getLastName().isBlank()) {
                rejectedByName = (rejectedByName != null ? rejectedByName : "")
                        + " " + regularization.getRejectedBy().getLastName();
            }
        }

        List<RegularizationDetailResponse> detailResponses = new ArrayList<>();
        if (regularization.getDetails() != null) {
            detailResponses = regularization.getDetails().stream()
                    .map(this::toDetailResponse)
                    .toList();
        }

        return RegularizationResponse.builder()
                .id(regularization.getId())
                .employeeId(employeeId)
                .employeeName(employeeName)
                .fromDate(regularization.getFromDate())
                .toDate(regularization.getToDate())
                .reason(regularization.getReason())
                .status(regularization.getStatus())
                .requestedAt(regularization.getRequestedAt())
                .approvedAt(regularization.getApprovedAt())
                .approvedById(approvedById)
                .approvedByName(approvedByName)
                .rejectedAt(regularization.getRejectedAt())
                .rejectedById(rejectedById)
                .rejectedByName(rejectedByName)
                .rejectionReason(regularization.getRejectionReason())
                .createdAt(regularization.getCreatedAt())
                .details(detailResponses)
                .build();
    }

    public RegularizationDetailResponse toDetailResponse(AttendanceRegularizationDetail detail) {
        return RegularizationDetailResponse.builder()
                .id(detail.getId())
                .regularizationId(detail.getRegularization() != null
                        ? detail.getRegularization().getId() : null)
                .attendanceId(detail.getAttendance() != null
                        ? detail.getAttendance().getId() : null)
                .attendanceDate(detail.getAttendance() != null
                        ? detail.getAttendance().getAttendanceDate() : null)
                .originalStatus(detail.getOriginalStatus())
                .originalCheckIn(DateTimeUtil.formatTime(detail.getOriginalCheckIn()))
                .originalCheckOut(DateTimeUtil.formatTime(detail.getOriginalCheckOut()))
                .requestedStatus(detail.getRequestedStatus())
                .requestedCheckIn(DateTimeUtil.formatTime(detail.getRequestedCheckIn()))
                .requestedCheckOut(DateTimeUtil.formatTime(detail.getRequestedCheckOut()))
                .approvedStatus(detail.getApprovedStatus())
                .approvedCheckIn(DateTimeUtil.formatTime(detail.getApprovedCheckIn()))
                .approvedCheckOut(DateTimeUtil.formatTime(detail.getApprovedCheckOut()))
                .status(detail.getStatus())
                .remarks(detail.getRemarks())
                .build();
    }
}
