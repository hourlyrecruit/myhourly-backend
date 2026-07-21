package com.my_hourly.attendance.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.repository.AttendanceBreakRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceValidationService;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.holiday.service.HolidayService;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceValidationServiceImpl
        implements AttendanceValidationService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceSettingsService attendanceSettingsService;
    private final HolidayService holidayService;
    private final AttendanceBreakRepository attendanceBreakRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public void validateCheckIn(Employee employee) {

        LocalDate today = LocalDate.now();

        AttendanceSettings settings =
                attendanceSettingsService.getSettings();

        validateDuplicateAttendance(employee, today);

        validateWeekend(today, settings);

        validateLeaveAttendance(employee, today);

        validateHoliday(today, settings);


    }

    private void validateDuplicateAttendance(
            Employee employee,
            LocalDate date
    ) {

        if (attendanceRepository.existsByEmployeeAndAttendanceDate(employee, date)) {

            throw new ValidationException(
                    "You have already checked in today.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

    }

    private void validateWeekend(
            LocalDate date,
            AttendanceSettings settings
    ) {

        if (Boolean.TRUE.equals(settings.getWeekendAttendanceAllowed())) {
            return;
        }

        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {

            throw new ValidationException(
                    "Attendance cannot be marked on weekends.",
                    ErrorCode.VALIDATION_FAILED
            );

        }

    }

    private void validateLeaveAttendance(Employee employee, LocalDate date) {

        attendanceRepository
                .findByEmployeeAndAttendanceDate(employee, date)
                .ifPresent(attendance -> {

                    if (attendance.getAttendanceStatus() == AttendanceStatus.LEAVE) {

                        throw new ValidationException(
                                "You are on approved leave today. Check-in is not allowed.",
                                ErrorCode.VALIDATION_FAILED
                        );

                    }

                });

    }

    private void validateHoliday(
            LocalDate date,
            AttendanceSettings settings
    ) {

        if (Boolean.TRUE.equals(settings.getHolidayAttendanceAllowed())) {
            return;
        }

        holidayService.getHolidayByDate(date)
                .ifPresent(holiday -> {

                    if (!holiday.getAttendanceAllowed()) {

                        throw new ValidationException(
                                "Attendance cannot be marked on " +
                                        holiday.getHolidayName(),
                                ErrorCode.VALIDATION_FAILED
                        );

                    }

                });

    }

    @Override
    public void validateCheckOut(Attendance attendance) {

        validateAlreadyCheckedOut(attendance);

        validateActiveBreak(attendance);


    }



    private void validateAlreadyCheckedOut(
            Attendance attendance
    ) {

        if (attendance.getCheckOutTime() != null) {

            throw new ValidationException(
                    "You have already checked out today.",
                    ErrorCode.VALIDATION_FAILED
            );

        }

    }

    private void validateActiveBreak(
            Attendance attendance
    ) {

        boolean activeBreak = attendanceBreakRepository
                .findFirstByAttendanceAndBreakEndTimeIsNullOrderByBreakStartTimeDesc(attendance)
                .isPresent();

        if (activeBreak) {

            throw new ValidationException(
                    "Please end your break before checking out.",
                    ErrorCode.VALIDATION_FAILED
            );

        }

    }

    @Override
    public void validateBreakStart(Attendance attendance) {
        // TODO: Implement later
    }

    @Override
    public void validateBreakEnd(Attendance attendance) {
        // TODO: Implement later
    }
}