package com.my_hourly.attendance.scheduler;

import com.my_hourly.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {
    private final AttendanceService attendanceService;


    @Scheduled(fixedRate = 60000) // runs every 1 minute
    public void checkMissedCheckouts() {

        attendanceService.markMissedCheckouts();
    }
}
