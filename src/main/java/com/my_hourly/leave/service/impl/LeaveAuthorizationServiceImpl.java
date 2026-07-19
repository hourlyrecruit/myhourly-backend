package com.my_hourly.leave.service.impl;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.mapper.LeaveRequestMapper;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeaveAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveAuthorizationServiceImpl
        implements LeaveAuthorizationService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeService employeeService;
    private final LeaveRequestMapper leaveRequestMapper;

    @Override
    public void validateManagerApproval(
            Employee manager,
            LeaveRequest leaveRequest) {

        Employee reportingManager =
                leaveRequest.getEmployee().getReportingManager();

        if (reportingManager == null) {
            throw new BadRequestException(
                    "Reporting manager is not assigned.",
                    ErrorCode.REPORTING_MANAGER_NOT_ASSIGNED);
        }

        if (!reportingManager.getId().equals(manager.getId())) {
            throw new BadRequestException(
                    "You are not authorized to approve this leave.",
                    ErrorCode.NOT_ALLOWED);
        }

    }

    @Override
    public void validateHrApproval(Employee employee) {

        if (employee.getUser() == null ||
                employee.getUser().getRole() == null) {

            throw new BadRequestException(
                    "User role not found.",
                    ErrorCode.NOT_ALLOWED);
        }

        RoleName role = employee.getUser()
                .getRole();


        if (role != RoleName.HR_ADMIN &&
                role != RoleName.SUPER_ADMIN) {

            throw new BadRequestException(
                    "Only HR can approve leave.",
                    ErrorCode.NOT_ALLOWED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getTeamLeaveRequests() {

        Employee manager = employeeService.getCurrentEmployee();

        return leaveRequestRepository
                .findByEmployeeReportingManager(manager)
                .stream()
                .map(leaveRequestMapper::toResponse)
                .toList();
    }

}
