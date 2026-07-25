package com.my_hourly.celebration.scheduler;

import com.my_hourly.celebration.repository.CelebrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CelebrationScheduler {

    private final CelebrationRepository celebrationRepository;

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void expirePosts() {
        celebrationRepository.expirePosts(LocalDateTime.now().minusHours(24));
    }
}