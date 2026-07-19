package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.api.response.LeaveTransactionResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveTransaction;
import com.my_hourly.leave.enums.LeaveTransactionType;
import com.my_hourly.leave.mapper.LeaveTransactionMapper;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.repository.LeaveTransactionRepository;
import com.my_hourly.leave.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveTransactionServiceImpl
        implements LeaveTransactionService {

    private final LeaveTransactionRepository leaveTransactionRepository;
    private final LeaveTransactionMapper mapper;
    private final EmployeeService employeeService;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    @Transactional
    public void createTransaction(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest,
            LeaveTransactionType transactionType,
            Integer days,
            Integer balanceBefore,
            Integer balanceAfter,
            String remarks) {

        LeaveTransaction transaction = LeaveTransaction.builder()
                .employee(leaveBalance.getEmployee())
                .leaveType(leaveBalance.getLeaveType())
                .leaveRequest(leaveRequest)
                .transactionType(transactionType)
                .days(days)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .remarks(remarks)
                .build();

        leaveTransactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTransactionResponse> getMyTransactions() {

        Employee employee = employeeService.getCurrentEmployee();

        return leaveTransactionRepository.findByEmployee(employee)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTransactionResponse> getEmployeeTransactions(
            Long employeeId) {

        return leaveTransactionRepository.findByEmployeeId(employeeId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTransactionResponse> getLeaveTransactions(
            Long leaveRequestId) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave Request id {leaveRequestId}", ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return leaveTransactionRepository.findByLeaveRequest(leaveRequest)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

}