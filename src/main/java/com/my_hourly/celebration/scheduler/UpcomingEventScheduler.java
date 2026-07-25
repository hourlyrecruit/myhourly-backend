package com.my_hourly.celebration.scheduler;

import com.my_hourly.celebration.repository.UpcomingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UpcomingEventScheduler {
    private final UpcomingEventRepository eventRepository;
    /**
     * Runs every day at 12:00 AM.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredEvents() {
        eventRepository.deleteByEventDateLessThanEqual(LocalDate.now());
    }
}