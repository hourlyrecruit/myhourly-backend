package com.my_hourly.settings.notification.repository;

import com.my_hourly.settings.notification.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findFirstByActiveTrue();

}