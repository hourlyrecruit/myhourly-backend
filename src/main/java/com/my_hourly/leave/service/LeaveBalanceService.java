package com.my_hourly.leave.service;

import com.my_hourly.leave.dto.response.LeaveBalanceResponse;

public interface LeaveBalanceService {

    // =====================================================
    // Employee - View Current Month Leave Balance
    // =====================================================

    LeaveBalanceResponse getLeaveBalance(
            String employeeCode);

    // =====================================================
    // Monthly Leave Reset
    // =====================================================

    void monthlyLeaveReset();

}