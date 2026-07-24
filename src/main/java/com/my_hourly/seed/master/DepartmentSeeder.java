package com.my_hourly.seed.master;

import com.my_hourly.master.entity.Department;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentSeeder {

    private final DepartmentRepository departmentRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
//        if (departmentRepository.count() > 1) { // 1 is from DataInitializer
//            log.info("Departments already seeded. Skipping...");
//            return;
//        }

        List<Map<String, String>> records = csvReader.readCsv("seed/departments.csv");
        for (Map<String, String> record : records) {
            String deptCode = record.get("department_code");
            if (!departmentRepository.existsByDepartmentName(record.get("department_name"))) {
                Department department = Department.builder()
                        .departmentCode(deptCode)
                        .departmentName(record.get("department_name"))
                        .description(record.get("description"))
                        .active(true)
                        .build();
                departmentRepository.save(department);
                log.info("Seeded department: {}", deptCode);
            }
        }
    }
}

