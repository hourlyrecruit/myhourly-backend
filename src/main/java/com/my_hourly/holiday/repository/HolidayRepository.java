package com.my_hourly.holiday.repository;

import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>,
        JpaSpecificationExecutor<Holiday> {

    Optional<Holiday> findByHolidayDate(LocalDate holidayDate);

    boolean existsByHolidayDate(LocalDate holidayDate);

    List<Holiday> findByHolidayDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    long countByHolidayTypeAndHolidayDateBetween(
            HolidayType holidayType,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Holiday> findByHolidayDateGreaterThanEqualAndActiveTrueOrderByHolidayDateAsc(
            LocalDate holidayDate
    );

    boolean existsByHolidayName(String independenceDay);


}