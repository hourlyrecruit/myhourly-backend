package com.my_hourly.leave.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.enums.EmploymentType;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.request.LeaveRequest;
import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.entity.Leave;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.exception.LeaveAlreadyAppliedException;
import com.my_hourly.leave.exception.LeaveBalanceException;
import com.my_hourly.leave.exception.LeaveNotFoundException;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRepository;
import com.my_hourly.leave.service.LeaveService;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    // =====================================================
    // Employee - Apply Leave
    // =====================================================

    @Override
    public LeaveResponse applyLeave(LeaveRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() ->
                        new LeaveNotFoundException(
                                "Employee Not Found"));

        // Prevent Duplicate Leave

        if (leaveRepository.existsByEmployeeCodeAndFromDateAndToDate(
                request.getEmployeeCode(),
                request.getFromDate(),
                request.getToDate())) {

            throw new LeaveAlreadyAppliedException(
                    "Leave Already Applied.");
        }

        // Intern Cannot Apply Leave

        if (employee.getEmploymentType()
                == EmploymentType.INTERN) {

            throw new LeaveBalanceException(
                    "Interns Are Not Eligible For Leave.");
        }

        // Check Leave Balance

        LeaveBalance leaveBalance =
                leaveBalanceRepository
                .findByEmployeeCode(employee.getEmployeeCode())
                .orElseThrow(() ->
                        new LeaveBalanceException(
                                "Leave Balance Not Found"));

        if (leaveBalance.getAvailableLeave() <= 0) {

            throw new LeaveBalanceException(
                    "No Leave Balance Available.");
        }

        Leave leave = new Leave();

        leave.setEmployee(employee);
        leave.setEmployeeCode(employee.getEmployeeCode());
        leave.setEmployeeFirstName(employee.getFirstName());
        leave.setEmployeeLastName(employee.getLastName());

        leave.setLeaveType(request.getLeaveType());
        leave.setFromDate(request.getFromDate());
        leave.setToDate(request.getToDate());
        leave.setReason(request.getReason());

        leave.setStatus(LeaveStatus.PENDING);

        Leave savedLeave = leaveRepository.save(leave);

        return mapToResponse(savedLeave);
    }

    // =====================================================
    // Entity -> Response
    // =====================================================

    private LeaveResponse mapToResponse(
            Leave leave) {

        LeaveResponse response =
                new LeaveResponse();

        response.setId(leave.getId());

        response.setEmployeeCode(
                leave.getEmployeeCode());

        response.setEmployeeFirstName(
                leave.getEmployeeFirstName());

        response.setEmployeeLastName(
                leave.getEmployeeLastName());

        response.setLeaveType(
                leave.getLeaveType());

        response.setFromDate(
                leave.getFromDate());

        response.setToDate(
                leave.getToDate());

        response.setReason(
                leave.getReason());

        response.setStatus(
                leave.getStatus());

        return response;
    }
    
    
    // =====================================================
    // Employee - View Own Leave History
    // =====================================================

    @Override
    public List<LeaveResponse> getEmployeeLeaves(
            String employeeCode) {

        List<Leave> leaveList =
                leaveRepository.findByEmployeeCode(employeeCode);

        List<LeaveResponse> responseList =
                new ArrayList<>();

        for (Leave leave : leaveList) {

            responseList.add(mapToResponse(leave));

        }

        return responseList;
    }

    // =====================================================
    // Employee - View Leave Status
    // =====================================================

    @Override
    public List<LeaveResponse> getLeaveStatus(
            String employeeCode) {

        List<Leave> leaveList =
                leaveRepository.findByEmployeeCode(employeeCode);

        List<LeaveResponse> responseList =
                new ArrayList<>();

        for (Leave leave : leaveList) {

            responseList.add(mapToResponse(leave));

        }

        return responseList;
    }
    
    
    
    // =====================================================
    // Manager - View Pending Leaves
    // =====================================================

    @Override
    public List<LeaveResponse> getPendingLeaves() {

        List<Leave> leaveList =
                leaveRepository.findByStatus(LeaveStatus.PENDING);

        List<LeaveResponse> responseList =
                new ArrayList<>();

        for (Leave leave : leaveList) {

            responseList.add(mapToResponse(leave));

        }

        return responseList;
    }

    // =====================================================
    // Manager - Approve Leave
    // =====================================================

    @Override
    public LeaveResponse approveLeave(Long leaveId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() ->
                        new LeaveNotFoundException("Leave Not Found"));

        leave.setStatus(LeaveStatus.APPROVED);

        LeaveBalance leaveBalance =
                leaveBalanceRepository
                        .findByEmployeeCode(leave.getEmployeeCode())
                        .orElseThrow(() ->
                                new LeaveBalanceException(
                                        "Leave Balance Not Found"));

        leaveBalance.setUsedLeave(
                leaveBalance.getUsedLeave() + 1);

        leaveBalance.setAvailableLeave(
                leaveBalance.getAvailableLeave() - 1);

        leaveBalanceRepository.save(leaveBalance);

        Leave updatedLeave =
                leaveRepository.save(leave);

        return mapToResponse(updatedLeave);
    }

    // =====================================================
    // Manager - Reject Leave
    // =====================================================

    @Override
    public LeaveResponse rejectLeave(Long leaveId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() ->
                        new LeaveNotFoundException("Leave Not Found"));

        leave.setStatus(LeaveStatus.REJECTED);

        Leave updatedLeave =
                leaveRepository.save(leave);

        return mapToResponse(updatedLeave);
    }
    
    // =====================================================
    // HR - View All Leaves
    // =====================================================

    @Override
    public List<LeaveResponse> getAllLeaves() {

        List<Leave> leaveList = leaveRepository.findAll();

        List<LeaveResponse> responseList = new ArrayList<>();

        for (Leave leave : leaveList) {

            responseList.add(mapToResponse(leave));

        }

        return responseList;
    }

    // =====================================================
    // HR - View Approved Leaves
    // =====================================================

    @Override
    public List<LeaveResponse> getApprovedLeaves() {

        List<Leave> leaveList =
                leaveRepository.findByStatus(LeaveStatus.APPROVED);

        List<LeaveResponse> responseList = new ArrayList<>();

        for (Leave leave : leaveList) {

            responseList.add(mapToResponse(leave));

        }

        return responseList;
    }

    // =====================================================
    // HR - View Rejected Leaves
    // =====================================================

    @Override
    public List<LeaveResponse> getRejectedLeaves() {

        List<Leave> leaveList =
                leaveRepository.findByStatus(LeaveStatus.REJECTED);

        List<LeaveResponse> responseList = new ArrayList<>();

        for (Leave leave : leaveList) {

            responseList.add(mapToResponse(leave));

        }

        return responseList;
    }

}
    
    

    