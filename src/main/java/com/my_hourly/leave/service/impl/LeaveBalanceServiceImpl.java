package com.my_hourly.leave.service.impl;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveAllocationType;
import com.my_hourly.leave.enums.LeaveTransactionType;
import com.my_hourly.leave.enums.MonthType;
import com.my_hourly.leave.mapper.LeaveBalanceMapper;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
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
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceEntity(
            Employee employee,
            LeaveType leaveType,
            LocalDate date) {

        Integer year = date.getYear();

        MonthType month = leaveType.getAllocationType() == LeaveAllocationType.MONTHLY
                ? MonthType.valueOf(date.getMonth().name())
                : null;

        return leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYearAndMonth(
                        employee,
                        leaveType,
                        year,
                        month)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave Balance employee ID: " + employee.getEmployeeCode()
                                , ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceResponse getLeaveBalance(
            Long leaveBalanceId) {

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
    public List<LeaveBalanceResponse> getEmployeeLeaveBalances(
            Long employeeId) {

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

    @Override
    @Transactional
    public void initializeEmployeeLeaveBalance(Employee employee) {

        if (leaveBalanceRepository.existsByEmployee(employee)) {
            log.warn("Leave balances already exist for employee {}", employee.getEmployeeCode());
            return;
        }

        LocalDate joiningDate = employee.getDateOfJoining();

        Integer year = joiningDate.getYear();

        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        List<LeaveBalance> balances = leaveTypes.stream()
                .map(leaveType -> {

                    MonthType month = leaveType.getAllocationType() == LeaveAllocationType.MONTHLY
                            ? MonthType.valueOf(joiningDate.getMonth().name())
                            : null;

                    return LeaveBalance.builder()
                            .employee(employee)
                            .leaveType(leaveType)
                            .year(year)
                            .month(month)
                            .allocatedLeaves(leaveType.getAllocatedDays())
                            .carriedForwardLeaves(0)
                            .usedLeaves(0)
                            .expiredLeaves(0)
                            .remainingLeaves(leaveType.getAllocatedDays())
                            .build();
                })
                .toList();

        leaveBalanceRepository.saveAll(balances);
    }

}
