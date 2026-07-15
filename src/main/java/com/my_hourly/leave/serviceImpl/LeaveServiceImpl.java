package com.my_hourly.leave.serviceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.request.LeaveRequest;
import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.entity.Leave;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.exception.LeaveAlreadyAppliedException;
import com.my_hourly.leave.exception.LeaveNotFoundException;
import com.my_hourly.leave.repository.LeaveRepository;
import com.my_hourly.leave.service.LeaveService;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final int TOTAL_LEAVES = 20;

    // =====================================================
    // Employee - Apply Leave
    // =====================================================

    @Override
    public LeaveResponse applyLeave(LeaveRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() ->
                        new RuntimeException("Employee Not Found"));

        if (leaveRepository.existsByEmployeeCodeAndFromDateAndToDate(
                request.getEmployeeCode(),
                request.getFromDate(),
                request.getToDate())) {

            throw new LeaveAlreadyAppliedException(
                    "Leave already applied for selected dates.");
        }

        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new RuntimeException(
                    "From Date cannot be greater than To Date.");
        }

        Leave leave = new Leave();

        leave.setEmployee(employee);
        leave.setEmployeeCode(employee.getEmployeeCode());
        leave.setEmployeeName(employee.getName());

        leave.setFromDate(request.getFromDate());
        leave.setToDate(request.getToDate());

        long days = ChronoUnit.DAYS.between(
                request.getFromDate(),
                request.getToDate()) + 1;

        leave.setTotalDays((int) days);

        leave.setStatus(LeaveStatus.PENDING);
        leave.setAppliedDate(LocalDate.now());

        Leave savedLeave = leaveRepository.save(leave);

        return mapToResponse(savedLeave);
    }

    // =====================================================
    // Employee - View Own Leaves
    // =====================================================

    @Override
    public List<LeaveResponse> getEmployeeLeaves(String employeeCode) {

        List<Leave> leaveList =
                leaveRepository.findByEmployeeCode(employeeCode);

        List<LeaveResponse> responseList = new ArrayList<>();

        for (Leave leave : leaveList) {
            responseList.add(mapToResponse(leave));
        }

        return responseList;
    }

    // =====================================================
    // Employee - View Leave Status
    // =====================================================

    @Override
    public List<LeaveResponse> getLeaveStatus(String employeeCode) {

        List<Leave> leaveList =
                leaveRepository.findByEmployeeCode(employeeCode);

        List<LeaveResponse> responseList = new ArrayList<>();

        for (Leave leave : leaveList) {
            responseList.add(mapToResponse(leave));
        }

        return responseList;
    }

    // =====================================================
    // Employee - View Leave Balance
    // =====================================================

    @Override
    public Integer getLeaveBalance(String employeeCode) {

        long approvedLeaves =
                leaveRepository.countByEmployeeCodeAndStatus(
                        employeeCode,
                        LeaveStatus.APPROVED);

        return TOTAL_LEAVES - (int) approvedLeaves;
    }
    
    
    // =====================================================
    // Manager / HR - View All Leaves
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
    // Manager - View Pending Leaves
    // =====================================================

    @Override
    public List<LeaveResponse> getPendingLeaves() {

        List<Leave> leaveList =
                leaveRepository.findByStatus(LeaveStatus.PENDING);

        List<LeaveResponse> responseList = new ArrayList<>();

        for (Leave leave : leaveList) {
            responseList.add(mapToResponse(leave));
        }

        return responseList;
    }

    // =====================================================
    // Manager - Approve Leave
    // =====================================================

    @Override
    public LeaveResponse approveLeave(
            Long leaveId,
            String managerRemarks) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() ->
                        new LeaveNotFoundException("Leave Not Found"));

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setManagerRemarks(managerRemarks);
        leave.setApprovedDate(LocalDate.now());

        Leave updatedLeave = leaveRepository.save(leave);

        return mapToResponse(updatedLeave);
    }

    // =====================================================
    // Manager - Reject Leave
    // =====================================================

    @Override
    public LeaveResponse rejectLeave(
            Long leaveId,
            String managerRemarks) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() ->
                        new LeaveNotFoundException("Leave Not Found"));

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerRemarks(managerRemarks);
        leave.setApprovedDate(LocalDate.now());

        Leave updatedLeave = leaveRepository.save(leave);

        return mapToResponse(updatedLeave);
    }

    // =====================================================
    // Entity -> Response
    // =====================================================

    private LeaveResponse mapToResponse(Leave leave) {

        LeaveResponse response = new LeaveResponse();

        response.setId(leave.getId());
        response.setEmployeeCode(leave.getEmployeeCode());
        response.setEmployeeName(leave.getEmployeeName());

        response.setFromDate(leave.getFromDate());
        response.setToDate(leave.getToDate());
        response.setTotalDays(leave.getTotalDays());

        response.setStatus(leave.getStatus());
        response.setManagerRemarks(leave.getManagerRemarks());

        response.setAppliedDate(leave.getAppliedDate());
        response.setApprovedDate(leave.getApprovedDate());

        return response;
    }

}
    
    
    
    
    