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
public class WorkAnniversaryScheduler {

    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 5 9 * * *")
    @Transactional
    public void sendWorkAnniversaryNotifications() {

        LocalDate today = LocalDate.now();

        List<Employee> allEmployees = employeeRepository.findByActiveTrue();

        List<Employee> anniversaryEmployees = allEmployees.stream()
                .filter(employee ->
                        employee.getDateOfJoining() != null
                                && employee.getDateOfJoining().getMonthValue() == today.getMonthValue()
                                && employee.getDateOfJoining().getDayOfMonth() == today.getDayOfMonth()
                )
                .toList();

        if (anniversaryEmployees.isEmpty()) {
            log.info("No work anniversaries found for {}", today);
            return;
        }

        for (Employee anniversaryEmployee : anniversaryEmployees) {

            String employeeName = anniversaryEmployee.getFirstName();

            if (anniversaryEmployee.getLastName() != null
                    && !anniversaryEmployee.getLastName().isBlank()) {
                employeeName += " " + anniversaryEmployee.getLastName();
            }

            // Wish the employee
            notificationService.createNotification(
                    anniversaryEmployee,
                    "Happy Work Anniversary 🎉",
                    "Congratulations on your work anniversary! Thank you for being a valuable part of the organization.",
                    NotificationType.WORK_ANNIVERSARY,
                    NotificationPriority.MEDIUM,
                    ReferenceType.EMPLOYEE,
                    anniversaryEmployee.getId()
            );

            // Notify everyone else
            for (Employee employee : allEmployees) {

                if (employee.getId().equals(anniversaryEmployee.getId())) {
                    continue;
                }

                notificationService.createNotification(
                        employee,
                        "Work Anniversary 🎊",
                        employeeName + " is celebrating a work anniversary today. Wish them well!",
                        NotificationType.WORK_ANNIVERSARY,
                        NotificationPriority.LOW,
                        ReferenceType.EMPLOYEE,
                        anniversaryEmployee.getId()
                );
            }
        }

        log.info("Successfully sent work anniversary notifications to {} employee(s).",
                anniversaryEmployees.size());
    }
}