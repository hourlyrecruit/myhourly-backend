package com.my_hourly.calendar.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.calendar.api.response.CalendarEventResponse;
import com.my_hourly.calendar.api.response.CalendarResponse;
import com.my_hourly.calendar.enums.CalendarEventType;
import com.my_hourly.calendar.enums.CalendarView;
import com.my_hourly.calendar.service.CalendarService;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final HolidayRepository holidayRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;

    @Override
    public CalendarResponse getCalendar(
            Integer month,
            Integer year,
            CalendarView view,
            List<CalendarEventType> eventTypes
    ) {

        LocalDate startDate = YearMonth.of(year, month).atDay(1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        Employee currentEmployee = employeeService.getCurrentEmployee();

        if (view == CalendarView.ORGANIZATION) {
            validateOrganizationCalendarAccess(currentEmployee);
        }

        List<CalendarEventResponse> events = new ArrayList<>();

        // Holidays
        events.addAll(
                getHolidayEvents(startDate, endDate)
        );

        // Birthdays
        events.addAll(
                getBirthdayEvents(
                        currentEmployee,
                        month,
                        year,
                        view
                )
        );

        // Work Anniversary
        events.addAll(
                getWorkAnniversaryEvents(
                        currentEmployee,
                        month,
                        year,
                        view
                )
        );

        // Leave
        events.addAll(
                getLeaveEvents(
                        currentEmployee,
                        startDate,
                        endDate,
                        view
                )
        );

        // Attendance (Personal Only)
        if (view == CalendarView.PERSONAL) {

            events.addAll(
                    getAttendanceEvents(
                            startDate,
                            endDate,
                            currentEmployee
                    )
            );

        }

        events = filterEvents(events, eventTypes);

        events.sort(
                Comparator.comparing(CalendarEventResponse::getEventDate)
                        .thenComparing(CalendarEventResponse::getTitle)
        );

        return CalendarResponse.builder()
                .year(year)
                .month(month)
                .events(events)
                .build();
    }

    private List<CalendarEventResponse> filterEvents(
            List<CalendarEventResponse> events,
            List<CalendarEventType> eventTypes
    ) {

        if (eventTypes == null || eventTypes.isEmpty()) {
            return events;
        }

        return events.stream()
                .filter(event ->
                        eventTypes.contains(event.getEventType())
                )
                .toList();

    }

    private void validateOrganizationCalendarAccess(
            Employee currentEmployee
    ) {

        RoleName roleName = currentEmployee.getUser().getRole();

        if (roleName != RoleName.SUPER_ADMIN
                && roleName != RoleName.HR_ADMIN
                && roleName != RoleName.MANAGER) {

            throw new AccessDeniedException(
                    "You are not authorized to access organization calendar."
            );
        }

    }

    private String getEmployeeName(
            Employee employee
    ) {

        String firstName = employee.getFirstName() == null
                ? ""
                : employee.getFirstName().trim();

        String lastName = employee.getLastName() == null
                ? ""
                : employee.getLastName().trim();

        return (firstName + " " + lastName).trim();

    }

    private List<CalendarEventResponse> getHolidayEvents(
            LocalDate startDate,
            LocalDate endDate
    ) {

        return holidayRepository.findByHolidayDateBetween(startDate, endDate)
                .stream()
                .map(holiday -> CalendarEventResponse.builder()
                        .eventDate(holiday.getHolidayDate())
                        .title(holiday.getHolidayName())
                        .description(holiday.getDescription())
                        .eventType(CalendarEventType.HOLIDAY)
                        .color("#F44336")
                        .referenceId(holiday.getId())
                        .build())
                .toList();

    }

    private List<CalendarEventResponse> getBirthdayEvents(
            Employee currentEmployee,
            Integer month,
            Integer year,
            CalendarView view
    ) {

        List<Employee> employees;

        if (view == CalendarView.PERSONAL) {
            employees = List.of(currentEmployee);
        } else {
            employees = employeeRepository.findByActiveTrue();
        }

        return employees.stream()

                .filter(employee ->
                        employee.getDateOfBirth() != null
                                && employee.getDateOfBirth().getMonthValue() == month)

                .map(employee -> {

                    LocalDate birthday = LocalDate.of(
                            year,
                            month,
                            employee.getDateOfBirth().getDayOfMonth()
                    );

                    return CalendarEventResponse.builder()
                            .eventDate(birthday)
                            .title(getEmployeeName(employee) + "'s Birthday")
                            .description("Birthday Celebration")
                            .eventType(CalendarEventType.BIRTHDAY)
                            .color("#FF9800")
                            .referenceId(employee.getId())
                            .build();

                })

                .toList();

    }

    private List<CalendarEventResponse> getWorkAnniversaryEvents(
            Employee currentEmployee,
            Integer month,
            Integer year,
            CalendarView view
    ) {

        List<Employee> employees;

        if (view == CalendarView.PERSONAL) {
            employees = List.of(currentEmployee);
        } else {
            employees = employeeRepository.findByActiveTrue();
        }

        return employees.stream()

                .filter(employee ->
                        employee.getDateOfJoining() != null
                                && employee.getDateOfJoining().getMonthValue() == month)

                .map(employee -> {

                    LocalDate anniversary = LocalDate.of(
                            year,
                            month,
                            employee.getDateOfJoining().getDayOfMonth()
                    );

                    int yearsCompleted =
                            year - employee.getDateOfJoining().getYear();

                    return CalendarEventResponse.builder()
                            .eventDate(anniversary)
                            .title(getEmployeeName(employee) + "'s Work Anniversary")
                            .description(yearsCompleted + " Year(s) Completed")
                            .eventType(CalendarEventType.WORK_ANNIVERSARY)
                            .color("#4CAF50")
                            .referenceId(employee.getId())
                            .build();

                })

                .toList();

    }

    private List<CalendarEventResponse> getLeaveEvents(
            Employee currentEmployee,
            LocalDate startDate,
            LocalDate endDate,
            CalendarView view
    ) {

        List<LeaveRequest> leaveRequests;

        if (view == CalendarView.PERSONAL) {

            leaveRequests = leaveRequestRepository.findByEmployee(currentEmployee)
                    .stream()
                    .filter(leave ->
                            !leave.getStartDate().isAfter(endDate)
                                    && !leave.getEndDate().isBefore(startDate))
                    .toList();

        } else {

            leaveRequests = leaveRequestRepository.findCalendarLeaves(
                    LeaveStatus.HR_APPROVED,
                    startDate,
                    endDate
            );

        }

        List<CalendarEventResponse> events = new ArrayList<>();

        for (LeaveRequest leave : leaveRequests) {

            LocalDate current = leave.getStartDate().isBefore(startDate)
                    ? startDate
                    : leave.getStartDate();

            LocalDate last = leave.getEndDate().isAfter(endDate)
                    ? endDate
                    : leave.getEndDate();

            while (!current.isAfter(last)) {

                events.add(
                        CalendarEventResponse.builder()
                                .eventDate(current)
                                .title(getEmployeeName(leave.getEmployee()) + " - Leave")
                                .description(
                                        leave.getLeaveType().getName()
                                                + " ("
                                                + leave.getStatus().name()
                                                + ")"
                                )
                                .eventType(CalendarEventType.LEAVE)
                                .color("#2196F3")
                                .referenceId(leave.getId())
                                .build()
                );

                current = current.plusDays(1);
            }

        }

        return events;

    }

    private List<CalendarEventResponse> getAttendanceEvents(
            LocalDate startDate,
            LocalDate endDate,
            Employee employee
    ) {

        return attendanceRepository
                .findByEmployeeAndAttendanceDateBetween(
                        employee,
                        startDate,
                        endDate
                )
                .stream()
                .map(attendance -> CalendarEventResponse.builder()
                        .eventDate(attendance.getAttendanceDate())
                        .title("Attendance")
                        .description(buildAttendanceDescription(attendance))
                        .eventType(CalendarEventType.ATTENDANCE)
                        .color(getAttendanceColor(attendance))
                        .referenceId(attendance.getId())
                        .build())
                .toList();

    }

    private String buildAttendanceDescription(
            Attendance attendance
    ) {

        String checkIn = attendance.getCheckInTime() == null
                ? "--"
                : attendance.getCheckInTime().toString();

        String checkOut = attendance.getCheckOutTime() == null
                ? "--"
                : attendance.getCheckOutTime().toString();

        return "Check In : "
                + checkIn
                + " | Check Out : "
                + checkOut;

    }

    private String getAttendanceColor(
            Attendance attendance
    ) {

        return switch (attendance.getAttendanceStatus()) {

            case PRESENT -> "#4CAF50";

            case LATE -> "#FF9800";

            case HALF_DAY -> "#FFC107";

            case ABSENT -> "#F44336";

            case LEAVE -> "#2196F3";

            default -> "#9E9E9E";
        };

    }
}