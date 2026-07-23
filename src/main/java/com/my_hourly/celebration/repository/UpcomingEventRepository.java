package com.my_hourly.celebration.repository;

import com.my_hourly.celebration.entity.UpcomingEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UpcomingEventRepository extends JpaRepository<UpcomingEvents,Long> {
    List<UpcomingEvents> findByEventDateAfterOrderByEventDateAsc(LocalDate date);
    void deleteByEventDateLessThanEqual(LocalDate date);
}
