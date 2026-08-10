package com.my_hourly.notification.repository;

import com.my_hourly.notification.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
