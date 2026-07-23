package com.my_hourly.seed.master;

import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DesignationSeeder {

    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (designationRepository.count() > 1) {
            log.info("Designations already seeded. Skipping...");
            return;
        }

        List<Map<String, String>> records = csvReader.readCsv("seed/designations.csv");
        for (Map<String, String> record : records) {
            String desigCode = record.get("designation_code");
            if (!designationRepository.existsByDesignationName(record.get("designation_name"))) {
                String deptCode = record.get("department_code");
                Optional<Department> deptOpt = departmentRepository.findByDepartmentCode(deptCode);
                
                if (deptOpt.isEmpty()) {
                    // Fallback to name if code search fails
                    deptOpt = departmentRepository.findByDepartmentName(deptCode);
                }

                if (deptOpt.isPresent()) {
                    Designation designation = Designation.builder()
                            .designationCode(desigCode)
                            .designationName(record.get("designation_name"))
                            .description(record.get("description"))
                            .department(deptOpt.get())
                            .active(true)
                            .build();
                    designationRepository.save(designation);
                    log.info("Seeded designation: {}", desigCode);
                } else {
                    log.warn("Department not found for code/name: {}", deptCode);
                }
            }
        }
    }
}

