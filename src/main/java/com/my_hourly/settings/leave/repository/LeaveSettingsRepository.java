package com.my_hourly.settings.leave.repository;

import com.my_hourly.settings.leave.entity.LeaveSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveSettingsRepository extends JpaRepository<LeaveSettings, Long> {

    Optional<LeaveSettings> findFirstByActiveTrue();

}
