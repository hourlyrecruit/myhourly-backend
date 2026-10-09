package com.my_hourly.leave.service.impl;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveTransactionType;
import com.my_hourly.leave.mapper.LeaveBalanceMapper;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.service.LeaveBalanceService;
import com.my_hourly.leave.service.LeaveTransactionService;
import com.my_hourly.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final EmployeeRepository employeeRepository;
    private final LeaveTransactionService leaveTransactionService;

    @Override
    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceEntity(
            Employee employee,
            LeaveType leaveType,
            LocalDate date) {

        Integer year = date.getYear();

        return leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYear(
                        employee,
                        leaveType,
                        year)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave balance not allocated for employee "
                                        + employee.getId()
                                        + ", leaveType " + leaveType.getName()
                                        + ", year " + year + ".",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    @Override
    @Transactional
    public LeaveBalance getLeaveBalanceEntityForUpdate(
            Employee employee,
            LeaveType leaveType,
            LocalDate date) {

        Integer year = date.getYear();

        return leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYearForUpdate(
                        employee,
                        leaveType,
                        year)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave balance not allocated for employee "
                                        + employee.getId()
                                        + ", leaveType " + leaveType.getName()
                                        + ", year " + year + ".",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceResponse getLeaveBalance(Long leaveBalanceId) {

        LeaveBalance leaveBalance = leaveBalanceRepository.findById(leaveBalanceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave Balance id: " + leaveBalanceId,
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return leaveBalanceMapper.toResponse(leaveBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getMyLeaveBalances() {

        User user = SecurityUtils.getCurrentUser();

        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return leaveBalanceRepository.findByEmployee(employee)
                .stream()
                .map(leaveBalanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getEmployeeLeaveBalances(Long employeeId) {

        return leaveBalanceRepository.findByEmployeeId(employeeId)
                .stream()
                .map(leaveBalanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getAllLeaveBalances() {

        return leaveBalanceRepository.findAll()
                .stream()
                .map(leaveBalanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deductLeaveBalance(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest) {

        int before = leaveBalance.getRemainingLeaves();

        leaveBalance.setUsedLeaves(
                leaveBalance.getUsedLeaves() + leaveRequest.getTotalDays());

        leaveBalance.setRemainingLeaves(
                before - leaveRequest.getTotalDays());

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createTransaction(
                leaveBalance,
                leaveRequest,
                LeaveTransactionType.LEAVE_APPROVED,
                leaveRequest.getTotalDays(),
                before,
                leaveBalance.getRemainingLeaves(),
                "Leave approved");
    }

    @Override
    @Transactional
    public void deductPaidLeaveDays(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest,
            int days) {

        if (days <= 0) {
            // Entirely LOP (or nothing to deduct) - the annual balance is
            // deliberately untouched and no transaction is recorded.
            log.debug("No PAID days to deduct for leave request {} ({} LOP)",
                    leaveRequest.getId(), leaveRequest.getTotalDays());
            return;
        }

        int before = leaveBalance.getRemainingLeaves();

        // Only path-invariant guard: the allocator never asks for more than the
        // balance holds, but a stale read must never produce a negative balance.
        if (days > before) {
            log.warn("PAID days ({}) exceed remaining balance ({}) for employee {} leaveType {}; "
                            + "clamping the deduction to the available balance",
                    days, before, leaveBalance.getEmployee().getId(),
                    leaveBalance.getLeaveType().getId());
            days = before;
        }

        leaveBalance.setUsedLeaves(leaveBalance.getUsedLeaves() + days);
        leaveBalance.setRemainingLeaves(before - days);

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createTransaction(
                leaveBalance,
                leaveRequest,
                LeaveTransactionType.LEAVE_APPROVED,
                days,
                before,
                leaveBalance.getRemainingLeaves(),
                "Leave approved (" + days + " PAID day(s))");
    }

    @Override
    @Transactional
    public void restoreLeaveBalance(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest) {

        int before = leaveBalance.getRemainingLeaves();

        leaveBalance.setUsedLeaves(
                leaveBalance.getUsedLeaves() - leaveRequest.getTotalDays());

        leaveBalance.setRemainingLeaves(
                before + leaveRequest.getTotalDays());

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createTransaction(
                leaveBalance,
                leaveRequest,
                LeaveTransactionType.LEAVE_CANCELLED,
                leaveRequest.getTotalDays(),
                before,
                leaveBalance.getRemainingLeaves(),
                "Leave cancelled");
    }
}
