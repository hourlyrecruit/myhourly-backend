package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveValidationServiceImpl
        implements LeaveValidationService {

    private final LeaveTypeService leaveTypeService;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveRequestRepository leaveRequestRepository;
    private final HolidayRepository holidayRepository;

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

        Integer totalDays =
                calculateLeaveDays(
                        request.getStartDate(),
                        request.getEndDate());

        LeaveBalance leaveBalance =
                validateLeaveBalance(
                        employee,
                        leaveType,
                        totalDays);

        return new LeaveApplicationContext(
                employee,
                leaveType,
                leaveBalance,
                totalDays);
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
                                        LeaveStatus.MANAGER_APPROVED,
                                        LeaveStatus.HR_APPROVED
                                ),
                                endDate,
                                startDate);

        if (exists) {
            throw new BadRequestException(
                    "Leave request already exists for the selected dates.", ErrorCode.LEAVE_ALREADY_EXIST);
        }
    }

    private Integer calculateLeaveDays(
            LocalDate startDate,
            LocalDate endDate) {

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
                    "No working days found between selected dates.", ErrorCode.RESOURCE_NOT_FOUND);
        }

        return totalDays;
    }

    private boolean isWeekend(LocalDate date) {

        return date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private LeaveBalance validateLeaveBalance(
            Employee employee,
            LeaveType leaveType,
            Integer totalDays) {

        LeaveBalance leaveBalance =
                leaveBalanceService.getLeaveBalanceEntity(
                        employee,
                        leaveType,
                        LocalDate.now());

        if (leaveBalance.getRemainingLeaves() < totalDays) {
            throw new BadRequestException(
                    "Insufficient leave balance.", ErrorCode.INSUFFICIENT);
        }

        return leaveBalance;
    }

}
