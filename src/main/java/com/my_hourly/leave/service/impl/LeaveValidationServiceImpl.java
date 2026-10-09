package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.context.LeaveApplicationContext;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeaveBalanceService;
import com.my_hourly.leave.service.LeaveTypeService;
import com.my_hourly.leave.service.LeaveValidationService;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveValidationServiceImpl
        implements LeaveValidationService {

    private final LeaveTypeService leaveTypeService;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveRequestRepository leaveRequestRepository;
    private final HolidayRepository holidayRepository;
    private final LeaveSettingsService leaveSettingsService;

    @Override
    public LeaveApplicationContext validateLeaveApplication(
            Employee employee,
            LeaveRequestRequest request) {

        LeaveType leaveType =
                validateLeaveType(request.getLeaveTypeId());

        validateLeaveDates(
                request.getStartDate(),
                request.getEndDate());

        validateLeaveOverlap(
                employee,
                request.getStartDate(),
                request.getEndDate());

        // Calculate leave days with sandwich expansion
        SandwichLeaveExpansion expansion = expandForSandwichLeave(
                request.getStartDate(),
                request.getEndDate());

        Integer totalDays = calculateLeaveDays(
                expansion.expandedStart(),
                expansion.expandedEnd(),
                expansion.forcedWorkingDays());

        LeaveBalance leaveBalance =
                resolveLeaveBalance(
                        employee,
                        leaveType);

        return new LeaveApplicationContext(
                employee,
                leaveType,
                leaveBalance,
                totalDays,
                expansion.forcedWorkingDays());
    }

    private LeaveType validateLeaveType(Long leaveTypeId) {

        LeaveType leaveType =
                leaveTypeService.getLeaveTypeEntity(
                        leaveTypeId);

        if (!Boolean.TRUE.equals(leaveType.getActive())) {
            throw new BadRequestException(
                    "Selected leave type is inactive.", ErrorCode.NOT_ALLOWED);
        }

        return leaveType;
    }

    private void validateLeaveDates(
            LocalDate startDate,
            LocalDate endDate) {

        LocalDate today = LocalDate.now();

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException(
                    "Start date cannot be after end date.", ErrorCode.VALIDATION_FAILED);
        }

        if (startDate.isBefore(today)) {
            throw new BadRequestException(
                    "Start date cannot be before today.", ErrorCode.VALIDATION_FAILED);
        }

        if (endDate.isBefore(today)) {
            throw new BadRequestException(
                    "End date cannot be before today.", ErrorCode.VALIDATION_FAILED);
        }
    }

    private void validateLeaveOverlap(
            Employee employee,
            LocalDate startDate,
            LocalDate endDate) {

        boolean exists =
                leaveRequestRepository
                        .existsByEmployeeAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employee,
                                List.of(
                                        LeaveStatus.PENDING,
                                        LeaveStatus.APPROVED
                                ),
                                endDate,
                                startDate);

        if (exists) {
            throw new BadRequestException(
                    "Another pending or approved leave request overlaps the selected dates.", ErrorCode.LEAVE_ALREADY_EXIST);
        }
    }

    private Integer calculateLeaveDays(
            LocalDate startDate,
            LocalDate endDate) {
        return calculateLeaveDays(startDate, endDate, Set.of());
    }

    /**
     * Calculates working days between startDate and endDate, including forced working days.
     * Forced working days (sandwich leave weekends) are counted even if they fall on weekends.
     * 
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @param forcedWorkingDays set of dates to count as working days even if they are weekends
     * @return total number of working days
     */
    private Integer calculateLeaveDays(
            LocalDate startDate,
            LocalDate endDate,
            Set<LocalDate> forcedWorkingDays) {

        Set<LocalDate> holidayDates =
                holidayRepository
                        .findByHolidayDateBetween(
                                startDate,
                                endDate)
                        .stream()
                        .map(Holiday::getHolidayDate)
                        .collect(Collectors.toSet());

        int totalDays = 0;

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            // Forced working days (sandwich weekends) are always counted,
            // regardless of weekend or public holiday status
            if (forcedWorkingDays.contains(current)) {
                totalDays++;
                current = current.plusDays(1);
                continue;
            }

            if (isWeekend(current)) {
                current = current.plusDays(1);
                continue;
            }

            if (holidayDates.contains(current)) {
                current = current.plusDays(1);
                continue;
            }

            totalDays++;

            current = current.plusDays(1);
        }

        if (totalDays == 0) {
            throw new BadRequestException(
                    "No working days found between selected dates.", 
                    ErrorCode.RESOURCE_NOT_FOUND);
        }

        return totalDays;
    }

    /**
     * Expands leave date range based on sandwich leave settings and identifies
     * weekend dates that should be counted as chargeable days.
     * 
     * Order of evaluation prevents double-counting:
     * 1. Friday+Monday rule (if both Friday AND Monday are in the original request)
     * 2. Friday-only rule (if Friday in range and Friday+Monday rule didn't fire)
     * 3. Monday-only rule (if Monday in range and Friday+Monday rule didn't fire)
     * 
     * @param startDate original start date from user request
     * @param endDate original end date from user request
     * @return SandwichLeaveExpansion containing expanded range and forced working days
     */
    private SandwichLeaveExpansion expandForSandwichLeave(LocalDate startDate, LocalDate endDate) {
        
        LeaveSettings settings;
        try {
            settings = leaveSettingsService.getSettings();
        } catch (ResourceNotFoundException e) {
            // If settings are missing, default to no sandwich leave expansion
            log.warn("Leave settings not found, sandwich leave rules disabled");
            return new SandwichLeaveExpansion(startDate, endDate, Set.of());
        }
        
        LocalDate expandedStart = startDate;
        LocalDate expandedEnd = endDate;
        Set<LocalDate> forcedWorkingDays = new HashSet<>();
        
        // Collect all dates in the request range (original request only)
        Set<LocalDate> requestDates = new HashSet<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            requestDates.add(current);
            current = current.plusDays(1);
        }
        
        // Rule 1: Friday+Monday sandwich (highest priority)
        boolean hasFridayMondaySandwich = false;
        if (Boolean.TRUE.equals(settings.getSandwichLeaveFridayMondayEnabled())) {
            for (LocalDate date : requestDates) {
                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    // Because we check DayOfWeek.FRIDAY, date.plusDays(1) is guaranteed to be
                    // Saturday, date.plusDays(2) is Sunday, and date.plusDays(3) is Monday
                    LocalDate saturday = date.plusDays(1);
                    LocalDate sunday = date.plusDays(2);
                    LocalDate monday = date.plusDays(3);
                    
                    if (requestDates.contains(monday)) {
                        // Friday+Monday sandwich detected: force Sat+Sun as working days
                        forcedWorkingDays.add(saturday);
                        forcedWorkingDays.add(sunday);
                        hasFridayMondaySandwich = true;
                        break;
                    }
                }
            }
        }
        
        // Rule 2: Friday-only sandwich
        // Only applies if Friday+Monday rule did NOT fire
        if (!hasFridayMondaySandwich && Boolean.TRUE.equals(settings.getSandwichLeaveFridayEnabled())) {
            for (LocalDate date : requestDates) {
                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    // Because we check DayOfWeek.FRIDAY, date.plusDays(1) is guaranteed to be
                    // Saturday and date.plusDays(2) is guaranteed to be Sunday (weekends)
                    LocalDate saturday = date.plusDays(1);
                    LocalDate sunday = date.plusDays(2);
                    
                    // Extend range and force Sat+Sun as working days
                    if (sunday.isAfter(expandedEnd)) {
                        expandedEnd = sunday;
                    }
                    forcedWorkingDays.add(saturday);
                    forcedWorkingDays.add(sunday);
                    break;
                }
            }
        }
        
        // Rule 3: Monday-only sandwich
        // Only applies if Friday+Monday rule did NOT fire
        if (!hasFridayMondaySandwich && Boolean.TRUE.equals(settings.getSandwichLeaveMondayEnabled())) {
            for (LocalDate date : requestDates) {
                if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    // Because we check DayOfWeek.MONDAY, date.minusDays(2) is guaranteed to be
                    // Saturday and date.minusDays(1) is guaranteed to be Sunday (weekends)
                    LocalDate saturday = date.minusDays(2);
                    LocalDate sunday = date.minusDays(1);
                    
                    // Extend range and force Sat+Sun as working days
                    if (saturday.isBefore(expandedStart)) {
                        expandedStart = saturday;
                    }
                    forcedWorkingDays.add(saturday);
                    forcedWorkingDays.add(sunday);
                    break;
                }
            }
        }
        
        return new SandwichLeaveExpansion(expandedStart, expandedEnd, forcedWorkingDays);
    }

    /**
     * Internal DTO for sandwich leave expansion results.
     * 
     * @param expandedStart potentially expanded start date (may be same as original)
     * @param expandedEnd potentially expanded end date (may be same as original)
     * @param forcedWorkingDays set of dates that must be counted as working days even if they are weekends
     */
    private record SandwichLeaveExpansion(
        LocalDate expandedStart,
        LocalDate expandedEnd,
        Set<LocalDate> forcedWorkingDays
    ) {}

    private boolean isWeekend(LocalDate date) {

        return date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * Resolves the annual balance a request will be applied against.
     *
     * <p>Submission intentionally does NOT require the balance to cover the
     * whole request any more. A request may exceed the monthly paid allowance
     * and/or the remaining annual balance; those days are classified as LOP at
     * approval instead of being rejected here. The balance must still exist,
     * because a missing allocation is a configuration problem, not a business
     * decision.</p>
     */
    private LeaveBalance resolveLeaveBalance(
            Employee employee,
            LeaveType leaveType) {

        return leaveBalanceService.getLeaveBalanceEntity(
                employee,
                leaveType,
                LocalDate.now());
    }

}
