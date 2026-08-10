package com.my_hourly.notification.service.impl;

import com.my_hourly.notification.entity.Announcement;
import com.my_hourly.notification.repository.AnnouncementRepository;
import com.my_hourly.notification.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> getAnnouncementsForToday() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return announcementRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                startOfDay,
                endOfDay
        );
    }
}

