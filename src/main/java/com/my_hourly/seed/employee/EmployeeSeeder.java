package com.my_hourly.seed.employee;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.employee.entity.Gender;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeSeeder {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final JobTitleRepository jobTitleRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (employeeRepository.count() > 0) {
            log.info("Employees already seeded. Skipping...");
            return;
        }

        List<Map<String, String>> records = csvReader.readCsv("seed/employees.csv");
        for (Map<String, String> record : records) {
            String employeeCode = record.get("employee_code");
            if (employeeRepository.existsByEmployeeCode(employeeCode)) {
                log.info("Employee {} already exists. Skipping.", employeeCode);
                continue;
            }

            String username = record.get("user_username");
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.warn("User '{}' not found for employee {}. Skipping.", username, employeeCode);
                continue;
            }

            String deptCode = record.get("department_code");
            Optional<Department> deptOpt = departmentRepository.findByDepartmentCode(deptCode);
            if (deptOpt.isEmpty()) {
                log.warn("Department '{}' not found for employee {}. Skipping.", deptCode, employeeCode);
                continue;
            }

            String desigCode = record.get("designation_code");
            Optional<Designation> desigOpt = designationRepository.findByDesignationCode(desigCode);
            if (desigOpt.isEmpty()) {
                log.warn("Designation '{}' not found for employee {}. Skipping.", desigCode, employeeCode);
                continue;
            }

            String jobTitleStr = record.get("job_title");
            Optional<JobTitle> jobTitleOpt = jobTitleRepository.findByJobTitle(jobTitleStr);
            if (jobTitleOpt.isEmpty()) {
                log.warn("Job title '{}' not found for employee {}. Skipping.", jobTitleStr, employeeCode);
                continue;
            }

            Employee employee = Employee.builder()
                    .employeeCode(employeeCode)
                    .firstName(record.get("first_name"))
                    .lastName(record.get("last_name"))
                    .email(record.get("email"))
                    .phoneNumber(record.get("phone_number"))
                    .gender(Gender.valueOf(record.get("gender")))
                    .dateOfBirth(LocalDate.parse(record.get("dob")))
                    .dateOfJoining(LocalDate.parse(record.get("doj")))
                    .employmentType(EmploymentType.valueOf(record.get("employment_type")))
                    .department(deptOpt.get())
                    .designation(desigOpt.get())
                    .jobTitle(jobTitleOpt.get())
                    .user(userOpt.get())
                    .roleName(userOpt.get().getRole())
                    .active(true)
                    .build();

            employeeRepository.save(employee);
            log.info("Seeded employee: {}", employeeCode);
        }
    }
}

