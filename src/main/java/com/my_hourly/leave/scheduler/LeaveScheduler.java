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
     * <p>Reports how much of each employee's monthly paid-leave guideline went
     * unused in the month that is ending (when carry-forward is disabled).
     * The unused allowance simply lapses with the calendar month - the next
     * month recomputes it from LeaveSettings - so NO annual balance is ever
     * modified and repeated runs are harmless.</p>
     *
     * <p>Reported usage:
     * <pre>
     *   paidThisMonth   = APPROVED PAID days attributed to the month by date
     *   unusedGuideline = max(0, monthlyGuideline - paidThisMonth)
     * </pre></p>
     */
    @Scheduled(cron = "0 30 23 L * *")
    public void monthEndLeaveExpiry() {

        log.info("Month-End Leave Allowance Review Started");

        leaveExpiryService.expireMonthlyUnused();

        log.info("Month-End Leave Allowance Review Completed");
    }

}
