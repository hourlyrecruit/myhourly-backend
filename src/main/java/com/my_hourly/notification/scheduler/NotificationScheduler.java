package com.my_hourly.notification.scheduler;

import com.my_hourly.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;

    /**
     * Process attendance (late/absent/missed-checkout) and leave notifications.
     * Dev: every 5 seconds. Prod: every 5 minutes.
     */
    //@Scheduled(cron = "0 */5 * * * *")   // prod: every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")     // dev:  every 5 seconds
    public void processEventNotifications() {

        log.info("Processing attendance notifications...");
        notificationService.processAttendanceNotifications();

        log.info("Processing leave notifications...");
        notificationService.processLeaveNotifications();
    }

    /**
     * Send a checkout reminder to all employees who have checked in but not yet checked out.
     * Runs once a day at 5:30 PM (30 min before the default office end time of 6:00 PM).
     * Adjust the cron expression if your officeEndTime differs.
     *
     * Cron: second minute hour day month weekday
     *   "0 30 17 * * MON-FRI" = 5:30 PM, Monday to Friday only
     */
    @Scheduled(cron = "0 30 20 * * MON-FRI")
    public void processCheckoutReminder() {

        log.info("Sending checkout reminders...");
        notificationService.processCheckoutReminderNotifications();
    }

    /**
     * Process birthday, work anniversary and holiday notifications.
     * Runs every day at 9:00 AM.
     */
//    @Scheduled(cron = "0 0 9 * * *")
//    public void processDailyNotifications() {
//
//        log.info("Processing birthday notifications...");
//        notificationService.processBirthdayNotifications();
//
//        log.info("Processing work anniversary notifications...");
//        notificationService.processWorkAnniversaryNotifications();
//
//        log.info("Processing holiday notifications...");
//        notificationService.processHolidayNotifications();
//    }
}
