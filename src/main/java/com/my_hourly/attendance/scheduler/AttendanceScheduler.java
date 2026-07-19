package com.my_hourly.attendance.scheduler;

import com.my_hourly.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class AttendanceScheduler {
private final AttendanceService attendanceService;

    @Scheduled(cron = "0 */10 * * * *")
    public void autoCheckoutEmployees() {
       // attendanceService.autoCheckoutEmployees();
    }
}