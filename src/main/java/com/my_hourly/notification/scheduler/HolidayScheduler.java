package com.my_hourly.notification.scheduler;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.holiday.repository.HolidayRepository;
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
public class HolidayScheduler {

    private final HolidayRepository holidayRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    /**
     * Today's Holiday Notification
     * Runs every day at 09:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    //@Scheduled(cron = "*/5 * * * * *")
    @Transactional
    public void sendTodayHolidayNotification() {

        LocalDate today = LocalDate.now();

        holidayRepository.findByHolidayDate(today)
                .ifPresent(holiday -> {

                    List<Employee> employees =
                            employeeRepository.findByActiveTrue();

                    List<NotificationItem> items = new ArrayList<>();

                    for (Employee employee : employees) {

                        items.add(new NotificationItem(
                                employee,
                                "Holiday Today 🎉",
                                "Today is " + holiday.getHolidayName()
                                        + ". Enjoy your holiday!",
                                NotificationType.HOLIDAY,
                                NotificationPriority.HIGH,
                                ReferenceType.HOLIDAY,
                                holiday.getId()
                        ));

                    }

                    notificationService.createNotificationsBulk(items);

                    log.info("Today's holiday notifications sent.");

                });

    }

    /**
     * Tomorrow Holiday Reminder
     * Runs every day at 06:00 PM
     */
    @Scheduled(cron = "0 0 18 * * *")
   // @Scheduled(cron = "*/5 * * * * *")
    @Transactional
    public void sendTomorrowHolidayReminder() {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        holidayRepository.findByHolidayDate(tomorrow)
                .ifPresent(holiday -> {

                    List<Employee> employees =
                            employeeRepository.findByActiveTrue();

                    List<NotificationItem> items = new ArrayList<>();

                    for (Employee employee : employees) {

                        items.add(new NotificationItem(
                                employee,
                                "Upcoming Holiday 📅",
                                "Tomorrow is " + holiday.getHolidayName()
                                        + ". Plan your work accordingly.",
                                NotificationType.HOLIDAY,
                                NotificationPriority.MEDIUM,
                                ReferenceType.HOLIDAY,
                                holiday.getId()
                        ));

                    }

                    notificationService.createNotificationsBulk(items);

                    log.info("Tomorrow holiday reminder sent.");

                });

    }

}
