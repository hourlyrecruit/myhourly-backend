package com.my_hourly.leave.scheduler;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveAllocationService;
import com.my_hourly.leave.service.LeaveExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveScheduler {

    private final LeaveAllocationService leaveAllocationService;
    private final LeaveExpiryService leaveExpiryService;
    private final LeaveTypeRepository leaveTypeRepository;

    /**
     * Runs on Jan 1st at 12:00 AM.
     * Allocates the full annual leave balance for every active employee.
     */
    @Scheduled(cron = "0 0 0 1 1 *")
    public void yearlyLeaveAllocation() {

        log.info("Yearly Leave Allocation Started");

        leaveAllocationService.allocateYearlyLeaves();

        log.info("Yearly Leave Allocation Completed");
    }

    /**
     * Runs at 11:30 PM on the last day of every month.
     *
     * <p>For each active employee and each leave type where carry-forward is disabled,
     * calculates unused guideline days and deducts them from the annual balance.</p>
     *
     * <p>Algorithm:
     * <pre>
     *   usedThisMonth    = sum of HR_APPROVED leave days in the month
     *   unusedGuideline  = max(0, monthlyGuideline - usedThisMonth)
     *   if unusedGuideline > 0 and carryForwardAllowed = false:
     *     expiredLeaves  += unusedGuideline
     *     remainingLeaves -= unusedGuideline
     * </pre></p>
     */
    @Scheduled(cron = "0 30 23 L * *")
    public void monthEndLeaveExpiry() {

        log.info("Month-End Leave Expiry Started");

        leaveExpiryService.expireMonthlyUnused();

        log.info("Month-End Leave Expiry Completed");
    }

}
