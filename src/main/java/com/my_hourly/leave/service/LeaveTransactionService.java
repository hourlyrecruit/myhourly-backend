package com.my_hourly.leave.service;

import com.my_hourly.leave.api.response.LeaveTransactionResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveTransactionType;

import java.util.List;

public interface LeaveTransactionService {

    void createTransaction(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest,
            LeaveTransactionType transactionType,
            Integer days,
            Integer balanceBefore,
            Integer balanceAfter,
            String remarks);

    List<LeaveTransactionResponse> getMyTransactions();

    List<LeaveTransactionResponse> getEmployeeTransactions(Long employeeId);

    List<LeaveTransactionResponse> getLeaveTransactions(Long leaveRequestId);

    void createAllocationTransaction(
            LeaveBalance leaveBalance,
            Integer allocatedLeaves
    );

    void createExpiryTransaction(
            LeaveBalance leaveBalance,
            Integer days
    );
}