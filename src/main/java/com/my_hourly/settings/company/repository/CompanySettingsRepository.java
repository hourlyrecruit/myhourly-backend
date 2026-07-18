package com.my_hourly.settings.company.repository;

import com.my_hourly.settings.company.entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanySettingsRepository
        extends JpaRepository<CompanySettings, Long> {

    Optional<CompanySettings> findFirstByActiveTrue();


}
