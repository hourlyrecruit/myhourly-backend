package com.my_hourly.settings.workLogs.repository;


import com.my_hourly.settings.workLogs.entity.WorkLogSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkLogSettingsRepository extends JpaRepository<WorkLogSettings, Long> {

    Optional<WorkLogSettings> findFirstByActiveTrue();

}
