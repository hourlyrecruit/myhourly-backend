package com.my_hourly.notification.service;

import com.my_hourly.notification.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    List<Announcement> getAnnouncementsForToday();
}
