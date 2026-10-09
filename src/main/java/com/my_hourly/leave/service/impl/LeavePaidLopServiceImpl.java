package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.leave.dto.PaidLopAllocation;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeavePaidLopService;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Monthly PAID / LOP classification.
 *
 * <p>Algorithm, per calendar month touched by the request, in date order:</p>
 * <pre>
 *   allowanceLeft = max(0, monthlyGuideline - alreadyPaidDaysInMonth)
 *   for each working day of the request in this month:
 *     if allowanceLeft &gt; 0 and balance.remaining &gt; 0:
 *       PAID; allowanceLeft--; balance.remaining--
 *     else:
 *       LOP
 * </pre>
 *
 * <p>Two invariants fall out of walking days rather than the whole request:</p>
 * <ul>
 *   <li>a month boundary resets the allowance, so a single request can be PAID
 *       in October and LOP in November (or vice versa);</li>
 *   <li>the balance can never go negative, because a day is only PAID when the
 *       balance still has a day to spend, and a request only ever spends what it
 *       finds - an exhausted month or balance degrades to LOP instead.</li>
 * </ul>
 *
 * <p>Working days use the same rule as
 * {@code LeaveValidationServiceImpl#calculateLeaveDays}: weekends and holidays
 * inside the request range are never counted, so the per-month buckets always
 * add up to the request's stored {@code totalDays}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LeavePaidLopServiceImpl implements LeavePaidLopService {

    /** Used only when LeaveSettings is unavailable and the leave type is silent. */
    private static final int DEFAULT_MONTHLY_GUIDELINE = 2;

    private final LeaveSettingsService leaveSettingsService;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final HolidayRepository holidayRepository;

    @Override
    public PaidLopAllocation classify(
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate) {

        int monthlyGuideline = resolveMonthlyGuideline(leaveType);

        Set<LocalDate> holidays = holidayDates(startDate, endDate);

        // Balance is resolved per year (a request may cross a year boundary) and
        // cached so the annual allowance is shared across the months of one year.
        Map<Integer, LeaveBalance> balancesByYear = new HashMap<>();
        Map<Integer, Integer> remainingByYear = new HashMap<>();

        List<PaidLopAllocation.MonthAllocation> months = new ArrayList<>();

        int paidDays = 0;
        int lopDays = 0;

        YearMonth firstMonth = YearMonth.from(startDate);
        YearMonth lastMonth = YearMonth.from(endDate);

        for (YearMonth month = firstMonth;
             !month.isAfter(lastMonth);
             month = month.plusMonths(1)) {

            LocalDate monthFrom = startDate.isAfter(month.atDay(1))
                    ? startDate
                    : month.atDay(1);

            LocalDate monthTo = endDate.isBefore(month.atEndOfMonth())
                    ? endDate
                    : month.atEndOfMonth();

            List<LocalDate> workingDays = workingDays(monthFrom, monthTo, holidays);
            if (workingDays.isEmpty()) {
                continue;
            }

            int alreadyPaid = alreadyPaidDaysInMonth(employee, leaveType, month);
            int allowanceLeft = Math.max(0, monthlyGuideline - alreadyPaid);
            int allowanceBeforeThis = allowanceLeft;

            int remaining = remainingLeaves(employee, leaveType, month, balancesByYear, remainingByYear);

            int monthPaid = 0;
            int monthLop = 0;

            for (LocalDate day : workingDays) {

                if (allowanceLeft > 0 && remaining > 0) {
                    monthPaid++;
                    paidDays++;
                    allowanceLeft--;
                    remaining--;
                } else {
                    monthLop++;
                    lopDays++;
                }
            }

            remainingByYear.put(month.getYear(), remaining);

            months.add(new PaidLopAllocation.MonthAllocation(
                    month,
                    workingDays.size(),
                    allowanceBeforeThis,
                    monthPaid,
                    monthLop
            ));
        }

        log.debug("Classified leave for employee {} leaveType {} {}..{} as {} PAID / {} LOP "
                        + "(guideline {} day(s)/month)",
                employee.getId(), leaveType.getName(), startDate, endDate,
                paidDays, lopDays, monthlyGuideline);

        return new PaidLopAllocation(paidDays, lopDays, List.copyOf(months));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * The monthly paid-leave guideline. LeaveSettings is the source of truth;
     * the leave type's own value and then a hard default are only used if the
     * settings row cannot be read, so an approval never mis-classifies silently.
     */
    private int resolveMonthlyGuideline(LeaveType leaveType) {

        try {
            LeaveSettings settings = leaveSettingsService.getSettings();
            if (settings.getMonthlyGuideline() != null && settings.getMonthlyGuideline() > 0) {
                return settings.getMonthlyGuideline();
            }
            log.warn("LeaveSettings has no usable monthlyGuideline; falling back to leave type {}", leaveType.getId());
        } catch (Exception e) {
            log.warn("Could not retrieve LeaveSettings while classifying leave; "
                    + "falling back to leave type {} configuration", leaveType.getId(), e);
        }

        Integer leaveTypeGuideline = leaveType.getMonthlyGuideline();
        if (leaveTypeGuideline != null && leaveTypeGuideline > 0) {
            return leaveTypeGuideline;
        }

        return DEFAULT_MONTHLY_GUIDELINE;
    }

    private Set<LocalDate> holidayDates(LocalDate startDate, LocalDate endDate) {

        return holidayRepository.findByHolidayDateBetween(startDate, endDate)
                .stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());
    }

    private List<LocalDate> workingDays(LocalDate from, LocalDate to, Set<LocalDate> holidays) {

        List<LocalDate> days = new ArrayList<>();

        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            if (isWeekend(day) || holidays.contains(day)) {
                continue;
            }
            days.add(day);
        }

        return days;
    }

    private boolean isWeekend(LocalDate date) {

        return date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * PAID days already approved (and deducted) in this calendar month. Only
     * APPROVED requests count, so pending and rejected requests never consume
     * the allowance.
     */
    private int alreadyPaidDaysInMonth(Employee employee, LeaveType leaveType, YearMonth month) {

        Integer alreadyPaid = leaveRequestRepository.sumPaidLeaveDaysInMonth(
                employee,
                leaveType,
                month.atDay(1),
                month.atEndOfMonth()
        );

        return alreadyPaid == null ? 0 : alreadyPaid;
    }

    /**
     * Remaining annual balance for the month's year, cached for the duration of
     * one classification so multiple months of the same year share a balance.
     *
     * <p>Within the caller's transaction this returns the same instance the
     * approval already locked with {@code ...ForUpdate}, so the running total
     * cannot drift from the row that is actually being written.</p>
     */
    private int remainingLeaves(Employee employee,
                                LeaveType leaveType,
                                YearMonth month,
                                Map<Integer, LeaveBalance> balancesByYear,
                                Map<Integer, Integer> remainingByYear) {

        Integer alreadyRemaining = remainingByYear.get(month.getYear());
        if (alreadyRemaining != null) {
            return alreadyRemaining;
        }

        LeaveBalance balance = balancesByYear.computeIfAbsent(
                month.getYear(),
                year -> leaveBalanceRepository
                        .findByEmployeeAndLeaveTypeAndYear(employee, leaveType, year)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Leave balance not allocated for employee "
                                        + employee.getId()
                                        + ", leaveType " + leaveType.getName()
                                        + ", year " + year + ".",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ))
        );

        int remaining = Math.max(0, balance.getRemainingLeaves());
        remainingByYear.put(month.getYear(), remaining);

        return remaining;
    }
}
