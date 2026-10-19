package com.my_hourly.notification.service;

import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    List<Announcement> getAnnouncementsForToday();
    List<NotificationResponse> getCelebrationWallForToday();

    /**
     * Deletes an announcement and the notifications that were broadcast for it.
     */
    void deleteAnnouncement(Long announcementId);
}
