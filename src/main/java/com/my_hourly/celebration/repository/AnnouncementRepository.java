package com.my_hourly.celebration.repository;

import com.my_hourly.celebration.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {
    List<Announcement> findByActiveTrueOrderByCreatedAtDesc();
    List<Announcement> findByActiveTrueAndCreatedAtBefore(LocalDateTime dateTime);
}
