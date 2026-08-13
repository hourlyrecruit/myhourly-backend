package com.my_hourly.notification.scheduler;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.notification.enums.NotificationPriority;
import com.my_hourly.notification.enums.NotificationType;
import com.my_hourly.notification.enums.ReferenceType;
import com.my_hourly.notification.service.NotificationService;
import com.my_hourly.notification.service.NotificationService.NotificationItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends a one-time "upcoming birthday" reminder to every active employee for
 * each colleague whose birthday falls within the next few days. Today's
 * birthdays are intentionally excluded — they are handled by
 * {@link BirthdayScheduler} on the day itself.
 *
 * <p>The reminder uses {@link NotificationType#GENERAL} (with
 * {@link ReferenceType#EMPLOYEE} pointing at the birthday employee) so it does
 * not collide with the {@link NotificationType#BIRTHDAY} notifications created
 * by {@link BirthdayScheduler} — the bulk duplicate check keys on
 * (employee, referenceType, notificationType, referenceId), so both can exist
 * for the same birthday.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpcomingBirthdayScheduler {

    /**
     * How many days ahead to look for birthdays. Today is excluded.
     */
    private static final int UPCOMING_BIRTHDAY_WINDOW_DAYS = 3;

    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    /**
     * Runs every day at 08:30 AM, before the daily birthday wishes at 10:00 AM.
     */
    @Scheduled(cron = "0 30 8 * * *")
    //@Scheduled(cron = "*/5 * * * * *")
    @Transactional
    public void sendUpcomingBirthdayReminders() {

        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(UPCOMING_BIRTHDAY_WINDOW_DAYS);

        List<Employee> allEmployees = employeeRepository.findByActiveTrue();

        List<Employee> upcomingBirthdayEmployees = allEmployees.stream()
                .filter(employee -> employee.getDateOfBirth() != null)
                .filter(employee -> {
                    LocalDate next = employee.getDateOfBirth()
                            .withYear(today.getYear());
                    if (next.isBefore(today)) {
                        next = next.plusYears(1);
                    }
                    return next.isAfter(today) && !next.isAfter(windowEnd);
                })
                .toList();

        if (upcomingBirthdayEmployees.isEmpty()) {
            log.info("No upcoming birthdays within {} days.", UPCOMING_BIRTHDAY_WINDOW_DAYS);
            return;
        }

        for (Employee birthdayEmployee : upcomingBirthdayEmployees) {

            LocalDate upcomingBirthday = birthdayEmployee.getDateOfBirth()
                    .withYear(today.getYear());
            if (upcomingBirthday.isBefore(today)) {
                upcomingBirthday = upcomingBirthday.plusYears(1);
            }

            long daysUntil = ChronoUnit.DAYS.between(today, upcomingBirthday);

            String employeeName = birthdayEmployee.getFirstName();
            if (birthdayEmployee.getLastName() != null
                    && !birthdayEmployee.getLastName().isBlank()) {
                employeeName += " " + birthdayEmployee.getLastName();
            }

            String dayLabel = daysUntil == 1 ? "day" : "days";

            List<NotificationItem> items = new ArrayList<>();

            for (Employee employee : allEmployees) {

                if (employee.getId().equals(birthdayEmployee.getId())) {
                    continue;
                }

                items.add(new NotificationItem(
                        employee,
                        "Upcoming Birthday 🎂",
                        employeeName + "'s birthday is in " + daysUntil
                                + " " + dayLabel
                                + ". Don't forget to wish them!",
                        NotificationType.GENERAL,
                        NotificationPriority.LOW,
                        ReferenceType.EMPLOYEE,
                        birthdayEmployee.getId()
                ));
            }

            notificationService.createNotificationsBulk(items);
        }

        log.info("Upcoming birthday reminders sent for {} employee(s).",
                upcomingBirthdayEmployees.size());
    }
}
