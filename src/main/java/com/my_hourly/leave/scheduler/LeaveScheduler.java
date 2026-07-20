package com.my_hourly.leave.scheduler;

import com.my_hourly.leave.service.LeaveAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveScheduler {

    private final LeaveAllocationService leaveAllocationService;

    /**
     * Runs on the 1st day of every month at 12:00 AM
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlyLeaveAllocation() {

        log.info("Monthly Leave Allocation Started");

        leaveAllocationService.allocateMonthlyLeaves();

        log.info("Monthly Leave Allocation Completed");

    }

    @Scheduled(cron = "0 0 0 1 1 *")
    public void yearlyLeaveAllocation() {

        log.info("Yearly Leave Allocation Started");

        leaveAllocationService.allocateYearlyLeaves();

        log.info("Yearly Leave Allocation Completed");

    }


    @Scheduled(cron = "0 30 23 L * *")
    public void expireLeaves() {

        log.info("Leave Expiry Started");

        leaveAllocationService.expireLeaves();

        log.info("Leave Expiry Completed");

    }

}
