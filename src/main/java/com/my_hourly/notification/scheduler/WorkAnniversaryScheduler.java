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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkAnniversaryScheduler {

    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    //@Scheduled(cron = "*/5 * * * * *")
    @Transactional
    public void sendWorkAnniversaryNotifications() {

        LocalDate today = LocalDate.now();

        List<Employee> anniversaryEmployees =
                employeeRepository.findActiveEmployeesWithWorkAnniversary(
                        today.getMonthValue(),
                        today.getDayOfMonth()
                );

        if (anniversaryEmployees.isEmpty()) {
            log.info("No work anniversaries found for {}", today);
            return;
        }

        List<Employee> allEmployees = employeeRepository.findByActiveTrue();

        for (Employee anniversaryEmployee : anniversaryEmployees) {

            String employeeName = anniversaryEmployee.getFirstName();

            if (anniversaryEmployee.getLastName() != null
                    && !anniversaryEmployee.getLastName().isBlank()) {
                employeeName += " " + anniversaryEmployee.getLastName();
            }

            List<NotificationItem> items = new ArrayList<>();

            // Wish the employee
            items.add(new NotificationItem(
                    anniversaryEmployee,
                    "Happy Work Anniversary 🎉",
                    "Congratulations on your work anniversary! Thank you for being a valuable part of the organization.",
                    NotificationType.WORK_ANNIVERSARY,
                    NotificationPriority.MEDIUM,
                    ReferenceType.EMPLOYEE,
                    anniversaryEmployee.getId()
            ));

            // Notify everyone else
            for (Employee employee : allEmployees) {

                if (employee.getId().equals(anniversaryEmployee.getId())) {
                    continue;
                }

                items.add(new NotificationItem(
                        employee,
                        "Work Anniversary 🎊",
                        employeeName + " is celebrating a work anniversary today. Wish them well!",
                        NotificationType.WORK_ANNIVERSARY,
                        NotificationPriority.LOW,
                        ReferenceType.EMPLOYEE,
                        anniversaryEmployee.getId()
                ));
            }

            notificationService.createNotificationsBulk(items);
        }

        log.info("Successfully sent work anniversary notifications to {} employee(s).",
                anniversaryEmployees.size());
    }
}