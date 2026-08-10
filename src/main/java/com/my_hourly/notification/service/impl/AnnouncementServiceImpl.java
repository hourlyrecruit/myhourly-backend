package com.my_hourly.notification.service.impl;

import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.entity.Announcement;
import com.my_hourly.notification.entity.Notification;
import com.my_hourly.notification.enums.ReferenceType;
import com.my_hourly.notification.mapper.NotificationMapper;
import com.my_hourly.notification.repository.AnnouncementRepository;
import com.my_hourly.notification.repository.NotificationRepository;
import com.my_hourly.notification.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
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

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getCelebrationWallForToday() {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<ReferenceType> excludedTypes = List.of(
                ReferenceType.ATTENDANCE,
                ReferenceType.LEAVE
        );

        List<Notification> notifications =
                notificationRepository
                        .findByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndReferenceTypeNotIn(
                                startOfDay,
                                endOfDay,
                                excludedTypes
                        );

        // Get announcement IDs
        Set<Long> announcementIds = notifications.stream()
                .filter(notification ->
                        ReferenceType.ANNOUNCEMENT.equals(notification.getReferenceType())
                                && notification.getReferenceId() != null
                                && notification.getReferenceId() > 0
                )
                .map(Notification::getReferenceId)
                .collect(Collectors.toSet());

        // Fetch announcements and their attachments
        Map<Long, List<String>> announcementAttachmentMap = new HashMap<>();

        if (!announcementIds.isEmpty()) {

            List<Announcement> announcements =
                    announcementRepository.findAllById(announcementIds);

            for (Announcement announcement : announcements) {
                announcementAttachmentMap.put(
                        announcement.getId(),
                        announcement.getAttachmentUrls()
                );
            }
        }

        // Convert Notification → NotificationResponse
        return notifications.stream()
                .map(notification -> {

                    List<String> urls = null;

                    if (ReferenceType.ANNOUNCEMENT.equals(notification.getReferenceType())
                            && notification.getReferenceId() != null) {

                        urls = announcementAttachmentMap.getOrDefault(
                                notification.getReferenceId(),
                                Collections.emptyList()
                        );
                    }

                    return notificationMapper.toResponse(
                            notification,
                            urls
                    );
                })
                .toList();
    }
}

