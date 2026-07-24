package com.my_hourly.seed.holiday;

import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.entity.HolidayType;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidaySeeder {

    private final HolidayRepository holidayRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (holidayRepository.count() > 1) {
            log.info("Holidays already seeded. Skipping...");
            return;
        }

        List<Map<String, String>> records = csvReader.readCsv("seed/holidays.csv");
        for (Map<String, String> record : records) {
            String name = record.get("holiday_name");
            if (!holidayRepository.existsByHolidayName(name)) {
                Holiday holiday = Holiday.builder()
                        .holidayDate(LocalDate.parse(record.get("holiday_date")))
                        .holidayName(name)
                        .holidayType(HolidayType.valueOf(record.get("holiday_type")))
                        .description(record.get("description"))
                        .attendanceAllowed(Boolean.parseBoolean(record.get("attendance_allowed")))
                        .recurring(Boolean.parseBoolean(record.get("recurring")))
                        .build();
                holidayRepository.save(holiday);
                log.info("Seeded holiday: {}", name);
            }
        }
    }
}
