package com.my_hourly.leave.service.impl;

import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceService;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.context.LeaveApplicationContext;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.ApprovalLevel;
import com.my_hourly.leave.enums.LeaveAction;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.mapper.LeaveRequestMapper;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveRequestServiceImpl
        implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveValidationService leaveValidationService;
    private final LeaveAuthorizationService leaveAuthorizationService;
    private final EmployeeService employeeService;
    private final LeaveBalanceService leaveBalanceService;
    private final AttendanceService attendanceService;
    private final LeaveApprovalService leaveApprovalService;
    private final AttendanceRepository attendanceRepository;



    @Override
    public LeaveRequestResponse applyLeave(
            LeaveRequestRequest request) {

        Employee employee =
                employeeService.getCurrentEmployee();

        LeaveApplicationContext context =
                leaveValidationService.validateLeaveApplication(
                        employee,
                        request);

        LeaveRequest leaveRequest =
                LeaveRequest.builder()
                        .employee(employee)
                        .leaveType(context.leaveType())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .totalDays(context.totalDays())
                        .reason(request.getReason().trim())
                        .status(LeaveStatus.PENDING)
                        .build();

        LeaveRequest saved =
                leaveRequestRepository.save(leaveRequest);

        return leaveRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveRequestResponse cancelLeave(Long leaveRequestId) {

        Employee employee = employeeService.getCurrentEmployee();

        LeaveRequest leaveRequest = getLeaveRequestEntity(leaveRequestId);

        if (!leaveRequest.getEmployee().getId().equals(employee.getId())) {
            throw new BadRequestException(
                    "You cannot cancel another employee's leave.",
                    ErrorCode.NOT_ALLOWED);
        }

        if (leaveRequest.getStatus() == LeaveStatus.CANCELLED) {
            throw new BadRequestException(
                    "Leave request is already cancelled.",
                    ErrorCode.LEAVE_ALREADY_CANCELLED);
        }

        if (leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new BadRequestException(
                    "Rejected leave cannot be cancelled.",
                    ErrorCode.NOT_ALLOWED);
        }

        if (leaveRequest.getStatus() == LeaveStatus.HR_APPROVED) {

            LeaveBalance leaveBalance =
                    leaveBalanceService.getLeaveBalanceEntity(
                            leaveRequest.getEmployee(),
                            leaveRequest.getLeaveType(),
                            leaveRequest.getStartDate());

            leaveBalanceService.restoreLeaveBalance(
                    leaveBalance,
                    leaveRequest);

            // Optional: restore attendance
            attendanceService.removeLeaveAttendance(leaveRequest);
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);

        LeaveRequest savedLeaveRequest =
                leaveRequestRepository.save(leaveRequest);

        return leaveRequestMapper.toResponse(savedLeaveRequest);
    }

    @Override
    public LeaveRequestResponse managerAction(
            Long leaveRequestId,
            LeaveActionRequest request) {

        Employee manager = employeeService.getCurrentEmployee();

        LeaveRequest leaveRequest = getLeaveRequestEntity(leaveRequestId);

        // Leave must be pending
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Leave request has already been processed.",
                    ErrorCode.LEAVE_ALREADY_PROCESSED);
        }

        // Manager cannot approve own leave
        if (leaveRequest.getEmployee().getId().equals(manager.getId())) {
            throw new BadRequestException(
                    "You cannot approve your own leave.",
                    ErrorCode.NOT_ALLOWED);
        }

        // Validate reporting manager
//        leaveAuthorizationService.validateManagerApproval(
//                manager,
//                leaveRequest);

        switch (request.getAction()) {

            case APPROVE -> {

                leaveRequest.setStatus(LeaveStatus.MANAGER_APPROVED);

                leaveApprovalService.createApproval(
                        leaveRequest,
                        manager,
                        ApprovalLevel.MANAGER,
                        LeaveAction.APPROVE,
                        request.getReason());
            }

            case REJECT -> {

                if (request.getReason() == null ||
                        request.getReason().isBlank()) {

                    throw new BadRequestException(
                            "Rejection reason is required.",
                            ErrorCode.REASON_REQUIRED);
                }

                leaveRequest.setStatus(LeaveStatus.REJECTED);

                leaveApprovalService.createApproval(
                        leaveRequest,
                        manager,
                        ApprovalLevel.MANAGER,
                        LeaveAction.REJECT,
                        request.getReason());
            }

            default -> throw new BadRequestException(
                    "Invalid leave action.",
                    ErrorCode.INVALID_REQUEST);
        }

        LeaveRequest savedLeaveRequest =
                leaveRequestRepository.save(leaveRequest);

        return leaveRequestMapper.toResponse(savedLeaveRequest);
    }

    @Override
    public LeaveRequestResponse hrAction(
            Long leaveRequestId,
            LeaveActionRequest request) {

        Employee hr = employeeService.getCurrentEmployee();

        // Validate HR authorization
        leaveAuthorizationService.validateHrApproval(hr);

        LeaveRequest leaveRequest = getLeaveRequestEntity(leaveRequestId);


        LocalDate date = leaveRequest.getStartDate();

        while (!date.isAfter(leaveRequest.getEndDate())) {

            if (attendanceRepository.existsByEmployeeAndAttendanceDate(
                    leaveRequest.getEmployee(),
                    date)) {

                throw new ValidationException(
                        "Attendance already exists on " + date +
                                ". Leave cannot be approved.",
                        ErrorCode.VALIDATION_FAILED
                );
            }

            date = date.plusDays(1);
        }

        // Only manager approved leave can be processed by HR
        if (leaveRequest.getStatus() != LeaveStatus.MANAGER_APPROVED) {
            throw new BadRequestException(
                    "Manager approval is required.",
                    ErrorCode.APPROVAL_PENDING);
        }

        switch (request.getAction()) {

            case APPROVE -> {

                // Optional validation
                if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
                    throw new BadRequestException(
                            "Leave cannot be approved after its start date.",
                            ErrorCode.INVALID_DATE);
                }

                LeaveBalance leaveBalance =
                        leaveBalanceService.getLeaveBalanceEntity(
                                leaveRequest.getEmployee(),
                                leaveRequest.getLeaveType(),
                                leaveRequest.getStartDate());

                // Deduct leave balance (also creates leave transaction)
                leaveBalanceService.deductLeaveBalance(
                        leaveBalance,
                        leaveRequest);

                // Mark attendance
                attendanceService.markLeaveAttendance(leaveRequest);

                // Update leave status
                leaveRequest.setStatus(LeaveStatus.HR_APPROVED);

                // Save approval history
                leaveApprovalService.createApproval(
                        leaveRequest,
                        hr,
                        ApprovalLevel.HR,
                        LeaveAction.APPROVE,
                        request.getReason());
            }

            case REJECT -> {

                if (request.getReason() == null ||
                        request.getReason().isBlank()) {

                    throw new BadRequestException(
                            "Rejection reason is required.",
                            ErrorCode.REASON_REQUIRED);
                }

                leaveRequest.setStatus(LeaveStatus.REJECTED);

                leaveApprovalService.createApproval(
                        leaveRequest,
                        hr,
                        ApprovalLevel.HR,
                        LeaveAction.REJECT,
                        request.getReason());
            }

            default -> throw new BadRequestException(
                    "Invalid leave action.",
                    ErrorCode.INVALID_REQUEST);
        }

        LeaveRequest savedLeaveRequest =
                leaveRequestRepository.save(leaveRequest);

        return leaveRequestMapper.toResponse(savedLeaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestEntity(Long leaveRequestId) {

        return leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave Request id: " + leaveRequestId,
                        ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponse getLeaveRequest(Long leaveRequestId) {

        LeaveRequest leaveRequest = getLeaveRequestEntity(leaveRequestId);

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getMyLeaveRequests() {

        Employee employee = employeeService.getCurrentEmployee();

        return leaveRequestRepository.findByEmployee(employee)
                .stream()
                .map(leaveRequestMapper::toResponse)
                .toList();
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

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getAllLeaveRequests() {

        return leaveRequestRepository.findAll()
                .stream()
                .map(leaveRequestMapper::toResponse)
                .toList();
    }
}
