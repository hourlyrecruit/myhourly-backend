package com.my_hourly.settings.attendance.repository;

import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceSettingsRepository
        extends JpaRepository<AttendanceSettings, Long> {

    Optional<AttendanceSettings> findFirstByActiveTrue();

}