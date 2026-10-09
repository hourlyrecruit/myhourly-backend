package com.my_hourly.leave.mapper;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    public LeaveRequestResponse toResponse(
            LeaveRequest entity) {

        if (entity == null) {
            return null;
        }

        Employee approvedBy = entity.getApprovedBy();

        return LeaveRequestResponse.builder()
                .id(entity.getId())

                .employeeId(entity.getEmployee().getId())
                .employeeCode(entity.getEmployee().getEmployeeCode())
                .employeeName(
                        entity.getEmployee().getFirstName()
                                + " "
                                + entity.getEmployee().getLastName())

                .leaveTypeId(entity.getLeaveType().getId())
                .leaveType(entity.getLeaveType().getName())

                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())

                .totalDays(entity.getTotalDays())

                .reason(entity.getReason())

                .status(entity.getStatus())

                .paidDays(entity.getPaidDays())
                .lopDays(entity.getLopDays())

                // Null until the request is approved.
                .approvedById(approvedBy != null ? approvedBy.getId() : null)
                .approvedByName(approvedBy != null ? fullName(approvedBy) : null)
                .approvedByCode(approvedBy != null ? approvedBy.getEmployeeCode() : null)

//                .rejectionReason(entity.getRejectionReason())

                .build();
    }

    private String fullName(Employee employee) {

        String lastName = employee.getLastName();

        return lastName == null || lastName.isBlank()
                ? employee.getFirstName()
                : employee.getFirstName() + " " + lastName;
    }
}