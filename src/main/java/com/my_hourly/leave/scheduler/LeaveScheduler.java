package com.my_hourly.leave.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.my_hourly.leave.service.LeaveBalanceService;

@Component
public class LeaveScheduler {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    // =====================================================
    // Monthly Leave Reset
    // Runs Every Month on 1st Day at 12:00 AM
    // =====================================================

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyLeaveBalance() {

        leaveBalanceService.monthlyLeaveReset();

        System.out.println("Monthly Leave Balance Reset Successfully.");

    }

}