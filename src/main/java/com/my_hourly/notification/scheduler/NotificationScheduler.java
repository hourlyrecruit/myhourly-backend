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
     * Process attendance and leave notifications.
     * Runs every 5 minutes.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void processEventNotifications() {

        log.info("Processing attendance notifications...");
        notificationService.processAttendanceNotifications();

        log.info("Processing leave notifications...");
        notificationService.processLeaveNotifications();
    }

    /**
     * Process birthday, work anniversary and holiday notifications.
     * Runs every day at 9:00 AM.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void processDailyNotifications() {

        log.info("Processing birthday notifications...");
        notificationService.processBirthdayNotifications();

        log.info("Processing work anniversary notifications...");
        notificationService.processWorkAnniversaryNotifications();

        log.info("Processing holiday notifications...");
        notificationService.processHolidayNotifications();
    }
}