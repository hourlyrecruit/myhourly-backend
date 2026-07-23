package com.my_hourly.celebration.scheduler;

import com.my_hourly.celebration.entity.Announcement;
import com.my_hourly.celebration.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementScheduler {

    private final AnnouncementRepository announcementRepository;

    @Transactional
    @Scheduled(cron = "0 */10 * * * *") // Runs every 10 minutes
    public void deactivateExpiredAnnouncements() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(24);
        List<Announcement> announcements = announcementRepository.findByActiveTrueAndCreatedAtBefore(expiryTime);
        if (announcements.isEmpty()) {
            return;
        }
        announcements.forEach(announcement -> announcement.setActive(false));
        announcementRepository.saveAll(announcements);
    }
}