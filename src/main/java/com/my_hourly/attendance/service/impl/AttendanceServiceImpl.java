package com.my_hourly.attendance.service.impl;

import com.my_hourly.attendance.api.request.BreakStartRequest;
import com.my_hourly.attendance.api.request.CheckInRequest;
import com.my_hourly.attendance.api.request.CheckOutRequest;
import com.my_hourly.attendance.api.response.*;
import com.my_hourly.attendance.entity.*;
import com.my_hourly.attendance.mapper.AttendanceMapper;
import com.my_hourly.attendance.repository.AttendanceBreakRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceService;
import com.my_hourly.attendance.service.AttendanceValidationService;
import com.my_hourly.attendance.util.TimeUtil;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.attendance.specification.AttendanceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final EmployeeService employeeService;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final AttendanceBreakRepository attendanceBreakRepository;
    private final AttendanceValidationService attendanceValidationService;
    private final AttendanceSettingsService attendanceSettingsService;

    @Override
    public CheckInResponse checkIn(
            CheckInRequest request
    ) {

        Employee employee = employeeService.getCurrentEmployee();
        attendanceValidationService.validateCheckIn(employee);
        LocalDate today = LocalDate.now();
        LocalDateTime checkInTime = LocalDateTime.now();

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(today)
                .checkInTime(LocalDateTime.now())
                .attendanceStatus(calculateAttendanceStatus(checkInTime))
                .lateMinutes(calculateLateMinutes(checkInTime))
                .employeeStatus(EmployeeStatus.WORKING)
                .workingMinutes(0)
                .totalBreakMinutes(0)
                .checkInLatitude(request.getLatitude())
                .checkInLongitude(request.getLongitude())
                .build();

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        return attendanceMapper.toCheckInResponse(savedAttendance);

    }

    @Override
    public CheckOutResponse checkOut(CheckOutRequest request) {

        Employee employee = employeeService.getCurrentEmployee();

        Attendance attendance = attendanceRepository
                .findByEmployeeAndAttendanceDate(employee, LocalDate.now())
                .orElseThrow(() ->
                        new ValidationException(
                                "You have not checked in today.",
                                ErrorCode.VALIDATION_FAILED
                        ));

        if(attendance.getAttendanceStatus()==AttendanceStatus.LEAVE){
            throw new ValidationException("You are on leave today", ErrorCode.ON_LEAVE);
        }

        attendanceValidationService.validateCheckOut(attendance);

        LocalDateTime checkOutTime = LocalDateTime.now();

        attendance.setCheckOutTime(checkOutTime);

        attendance.setEmployeeStatus(EmployeeStatus.CHECKED_OUT);

        attendance.setCheckOutLatitude(request.getLatitude());

        attendance.setCheckOutLongitude(request.getLongitude());

        attendance.setWorkingMinutes(
                calculateWorkingMinutes(attendance)
        );

        attendance.setEarlyExitMinutes(
                calculateEarlyExitMinutes(checkOutTime)
        );

        attendance.setOvertimeMinutes(
                calculateOvertimeMinutes(checkOutTime)
        );

        attendance.setAttendanceStatus(
                calculateFinalAttendanceStatus(attendance)
        );

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return attendanceMapper.toCheckOutResponse(savedAttendance);
    }

    private int calculateWorkingMinutes(Attendance attendance) {

        LocalDateTime endTime = attendance.getCheckOutTime();

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        int totalMinutes = (int) Duration.between(
                attendance.getCheckInTime(),
                endTime
        ).toMinutes();

        int breakMinutes = attendance.getTotalBreakMinutes() == null
                ? 0
                : attendance.getTotalBreakMinutes();

        return Math.max(totalMinutes - breakMinutes, 0);
    }

    private Attendance getTodayAttendance(Employee employee) {

        return attendanceRepository
                .findByEmployeeAndAttendanceDate(
                        employee,
                        LocalDate.now()
                )
                .orElseThrow(() ->
                        new ValidationException(
                                "You have not checked in today.",
                                ErrorCode.VALIDATION_FAILED
                        ));
    }

    private AttendanceBreak getActiveBreak(
            Attendance attendance
    ) {

        return attendanceBreakRepository
                .findFirstByAttendanceAndBreakEndTimeIsNullOrderByBreakStartTimeDesc(
                        attendance
                )
                .orElse(null);
    }

    @Override
    public BreakStartResponse startBreak(
            BreakStartRequest request
    ) {

        Employee employee = employeeService.getCurrentEmployee();

        Attendance attendance = getTodayAttendance(employee);

        if (attendance.getCheckOutTime() != null) {

            throw new ValidationException(
                    "You have already checked out.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        if (getActiveBreak(attendance) != null) {

            throw new ValidationException(
                    "A break is already in progress.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        AttendanceBreak attendanceBreak =
                AttendanceBreak.builder()
                        .attendance(attendance)
                        .breakType(request.getBreakType())
                        .breakStartTime(LocalDateTime.now())
                        .build();

        AttendanceBreak attendanceBreak1 = attendanceBreakRepository.save(attendanceBreak);

        attendance.setEmployeeStatus(EmployeeStatus.ON_BREAK);

        attendanceRepository.save(attendance);

        return attendanceMapper.toStartBreakResponse(attendanceBreak1);
    }

    @Override
    public BreakEndResponse endBreak() {

        Employee employee = employeeService.getCurrentEmployee();

        Attendance attendance = getTodayAttendance(employee);

        AttendanceBreak attendanceBreak =
                getActiveBreak(attendance);

        if (attendanceBreak == null) {

            throw new ValidationException(
                    "No active break found.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        LocalDateTime breakEnd = LocalDateTime.now();

        attendanceBreak.setBreakEndTime(breakEnd);

        int breakMinutes = (int) Duration.between(
                attendanceBreak.getBreakStartTime(),
                breakEnd
        ).toMinutes();

        attendanceBreak.setBreakMinutes(breakMinutes);

        AttendanceBreak attendanceBreak1 = attendanceBreakRepository.save(attendanceBreak);

        attendance.setTotalBreakMinutes(
                attendance.getTotalBreakMinutes() + breakMinutes
        );

        attendance.setEmployeeStatus(EmployeeStatus.WORKING);

        attendanceRepository.save(attendance);

        return attendanceMapper.toEndBreakResponse(attendanceBreak1);
    }

    private BreakType getCurrentBreakType(
            Attendance attendance
    ) {

        AttendanceBreak activeBreak =
                getActiveBreak(attendance);

        return activeBreak == null
                ? BreakType.NO_ACTIVE_BREAK
                : activeBreak.getBreakType();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getTodayAttendance() {

        Employee employee = employeeService.getCurrentEmployee();

        Attendance attendance = getTodayAttendance(employee);

        attendance.setWorkingMinutes(
                calculateWorkingMinutes(attendance)
        );

        BreakType currentBreakType =
                getCurrentBreakType(attendance);

        return attendanceMapper.toResponse(
                attendance,
                currentBreakType
        );
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> getAttendanceHistory(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            LocalDate fromDate,
            LocalDate toDate,
            AttendanceStatus status
    ) {

        Employee employee = employeeService.getCurrentEmployee();

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Attendance> specification =
                Specification.where(
                                AttendanceSpecification.hasEmployee(employee))
                        .and(
                                AttendanceSpecification.fromDate(fromDate))
                        .and(
                                AttendanceSpecification.toDate(toDate))
                        .and(
                                AttendanceSpecification.hasStatus(status));

        Page<Attendance> attendancePage =
                attendanceRepository.findAll(
                        specification,
                        pageable
                );

        List<AttendanceResponse> responses =
                attendancePage.getContent()
                        .stream()
                        .map(attendance -> {

                            attendance.setWorkingMinutes(
                                    calculateWorkingMinutes(attendance)
                            );

                            return attendanceMapper.toResponse(
                                    attendance,
                                    getCurrentBreakType(attendance)
                            );

                        })
                        .toList();

        return PageResponse.<AttendanceResponse>builder()
                .content(responses)
                .page(attendancePage.getNumber())
                .size(attendancePage.getSize())
                .totalElements(attendancePage.getTotalElements())
                .totalPages(attendancePage.getTotalPages())
                .last(attendancePage.isLast())
                .build();

    }


    @Override
    @Transactional(readOnly = true)
    public AttendanceMonthlySummaryResponse getMonthlySummary(
            Integer month,
            Integer year
    ) {

        Employee employee = employeeService.getCurrentEmployee();

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findByEmployeeAndAttendanceDateBetween(
                        employee,
                        startDate,
                        endDate
                );

        long presentDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.PRESENT)
                .count();

        long lateDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.LATE)
                .count();

        long halfDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.HALF_DAY)
                .count();

        long absentDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.ABSENT)
                .count();

        long leaveDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.LEAVE)
                .count();

        long holidayDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.HOLIDAY)
                .count();

        long weekendDays = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.WEEKEND)
                .count();

        int totalWorkingMinutes = attendances.stream()
                .mapToInt(a -> a.getWorkingMinutes() == null ? 0 : a.getWorkingMinutes())
                .sum();

        int averageWorkingMinutes =
                attendances.isEmpty()
                        ? 0
                        : totalWorkingMinutes / attendances.size();

        return AttendanceMonthlySummaryResponse.builder()
                .month(month)
                .year(year)
                .totalAttendanceDays((long) attendances.size())
                .presentDays(presentDays)
                .lateDays(lateDays)
                .halfDays(halfDays)
                .absentDays(absentDays)
                //.leaveDays(leaveDays)
               // .holidayDays(holidayDays)
              //  .weekendDays(weekendDays)
                //.totalWorkingMinutes(totalWorkingMinutes)
                .totalWorkingHours(TimeUtil.formatMinutes(totalWorkingMinutes))
               // .averageWorkingMinutes(averageWorkingMinutes)
                .averageWorkingHours(TimeUtil.formatMinutes(averageWorkingMinutes))
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceCalendarResponse> getAttendanceCalendar(
            Integer month,
            Integer year
    ) {

        Employee employee = employeeService.getCurrentEmployee();

        LocalDate today = LocalDate.now();

        if (month == null) {
            month = today.getMonthValue();
        }

        if (year == null) {
            year = today.getYear();
        }

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findByEmployeeAndAttendanceDateBetween(
                        employee,
                        startDate,
                        endDate
                );

        return attendances.stream()
                .map(attendanceMapper::toCalendarResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDashboardResponse getAttendanceDashboard() {

        Employee employee = employeeService.getCurrentEmployee();

        Optional<Attendance> optionalAttendance =
                attendanceRepository.findByEmployeeAndAttendanceDate(
                        employee,
                        LocalDate.now()
                );

        if (optionalAttendance.isEmpty()) {

            return AttendanceDashboardResponse.builder()
                    .checkedIn(false)
                    .checkedOut(false)
                    .workingMinutes(0)
                    .workingHours("0m")
                    .breakMinutes(0)
                    .breakHours("0m")
                    .build();
        }

        Attendance attendance = optionalAttendance.get();

        attendance.setWorkingMinutes(
                calculateWorkingMinutes(attendance)
        );

        return attendanceMapper.toDashboardResponse(
                attendance,
                getCurrentBreakType(attendance)
        );
    }

    @Override
    @Transactional
    public void markLeaveAttendance(LeaveRequest leaveRequest) {

        Employee employee = leaveRequest.getEmployee();

        LocalDate date = leaveRequest.getStartDate();

        while (!date.isAfter(leaveRequest.getEndDate())) {

            Attendance attendance = Attendance.builder()
                    .employee(employee)
                    .attendanceDate(date)
                    .attendanceStatus(AttendanceStatus.LEAVE)
                    .workingMinutes(0)
                    .totalBreakMinutes(0)
                    .build();

            attendanceRepository.save(attendance);

            date = date.plusDays(1);
        }
    }
    @Override
    @Transactional
    public void removeLeaveAttendance(LeaveRequest leaveRequest) {

        Employee employee = leaveRequest.getEmployee();

        LocalDate date = leaveRequest.getStartDate();

        while (!date.isAfter(leaveRequest.getEndDate())) {

            attendanceRepository
                    .findByEmployeeAndAttendanceDate(employee, date)
                    .ifPresent(attendanceRepository::delete);

            date = date.plusDays(1);
        }
    }


    private AttendanceStatus calculateAttendanceStatus(LocalDateTime checkInTime) {

        AttendanceSettings settings =
                attendanceSettingsService.getSettings();

        LocalTime officeStartTime = settings.getOfficeStartTime();

        LocalTime allowedTime = officeStartTime.plusMinutes(
                settings.getGracePeriodMinutes()
        );

        LocalTime currentTime = checkInTime.toLocalTime();

        if (currentTime.isAfter(allowedTime)) {
            return AttendanceStatus.LATE;
        }

        return AttendanceStatus.PRESENT;
    }

    private AttendanceStatus calculateFinalAttendanceStatus(
            Attendance attendance
    ) {

        AttendanceSettings settings =
                attendanceSettingsService.getSettings();

        int workingMinutes = attendance.getWorkingMinutes();

        if (workingMinutes >= settings.getMinimumWorkingMinutes()) {

            return attendance.getAttendanceStatus();

        }

        if (workingMinutes >= settings.getHalfDayWorkingMinutes()) {

            return AttendanceStatus.HALF_DAY;

        }

        return AttendanceStatus.ABSENT;

    }

    private int calculateLateMinutes(LocalDateTime checkInTime) {

        AttendanceSettings settings =
                attendanceSettingsService.getSettings();

        LocalTime officeStart =
                settings.getOfficeStartTime();

        LocalTime allowedTime =
                officeStart.plusMinutes(
                        settings.getGracePeriodMinutes()
                );

        LocalTime currentTime = checkInTime.toLocalTime();

        if (!currentTime.isAfter(allowedTime)) {
            return 0;
        }

        return (int) Duration.between(
                allowedTime,
                currentTime
        ).toMinutes();
    }


    private int calculateEarlyExitMinutes(LocalDateTime checkOutTime) {

        AttendanceSettings settings =
                attendanceSettingsService.getSettings();

        LocalTime officeEnd =
                settings.getOfficeEndTime();

        LocalTime checkout = checkOutTime.toLocalTime();

        if (!checkout.isBefore(officeEnd)) {
            return 0;
        }

        return (int) Duration.between(
                checkout,
                officeEnd
        ).toMinutes();
    }

    private int calculateOvertimeMinutes(LocalDateTime checkOutTime) {

        AttendanceSettings settings =
                attendanceSettingsService.getSettings();

        if (!Boolean.TRUE.equals(settings.getOvertimeEnabled())) {
            return 0;
        }

        LocalTime officeEndTime = settings.getOfficeEndTime();

        LocalTime checkoutTime = checkOutTime.toLocalTime();

        if (!checkoutTime.isAfter(officeEndTime)) {
            return 0;
        }

        return (int) Duration.between(
                officeEndTime,
                checkoutTime
        ).toMinutes();
    }
}
