package com.my_hourly.notification.scheduler;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.notification.enums.NotificationPriority;
import com.my_hourly.notification.enums.NotificationType;
import com.my_hourly.notification.enums.ReferenceType;
import com.my_hourly.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayScheduler {

    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    //@Scheduled(cron = "*/15 * * * * *")
    @Transactional
    public void sendBirthdayNotifications() {

        LocalDate today = LocalDate.now();

        List<Employee> allEmployees = employeeRepository.findByActiveTrue();

        List<Employee> birthdayEmployees = allEmployees.stream()
                .filter(employee ->
                        employee.getDateOfBirth() != null
                                && employee.getDateOfBirth().getMonthValue() == today.getMonthValue()
                                && employee.getDateOfBirth().getDayOfMonth() == today.getDayOfMonth()
                )
                .toList();

        if (birthdayEmployees.isEmpty()) {
            log.info("No birthdays found for {}", today);
            return;
        }

        for (Employee birthdayEmployee : birthdayEmployees) {

            String employeeName = birthdayEmployee.getFirstName();

            if (birthdayEmployee.getLastName() != null
                    && !birthdayEmployee.getLastName().isBlank()) {
                employeeName += " " + birthdayEmployee.getLastName();
            }

            // Birthday wish to employee
            notificationService.createNotification(
                    birthdayEmployee,
                    "Happy Birthday 🎉",
                    "Wishing you a wonderful birthday and a fantastic year ahead!",
                    NotificationType.BIRTHDAY,
                    NotificationPriority.MEDIUM,
                    ReferenceType.EMPLOYEE,
                    birthdayEmployee.getId()
            );

            // Notify everyone else
            for (Employee employee : allEmployees) {

                if (employee.getId().equals(birthdayEmployee.getId())) {
                    continue;
                }

                notificationService.createNotification(
                        employee,
                        "Birthday Celebration 🎂",
                        employeeName + " is celebrating a birthday today. Don't forget to wish them!",
                        NotificationType.BIRTHDAY,
                        NotificationPriority.LOW,
                        ReferenceType.EMPLOYEE,
                        birthdayEmployee.getId()
                );
            }
        }

        log.info("Birthday notifications sent successfully for {} employee(s).",
                birthdayEmployees.size());
    }
}