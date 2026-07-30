package com.my_hourly.seed.employee;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.entity.UserStatus;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
//@Component
@RequiredArgsConstructor
public class EmployeeSeeder {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final JobTitleRepository jobTitleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        List<Map<String, String>> records = csvReader.readCsv("seed/employees.csv");
        for (Map<String, String> record : records) {
            String employeeCode = record.get("employee_code");
            if (employeeRepository.existsByEmployeeCode(employeeCode)) {
                log.info("Employee {} already exists. Skipping.", employeeCode);
                continue;
            }

            String username = record.get("user_username");
            String email = record.get("email");

            // 1. Resolve User
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByEmail(email);
            }
            if (userOpt.isEmpty()) {
                log.info("Creating missing user '{}' for employee {}.", username, employeeCode);
                User newUser = User.builder()
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode("password123"))
                        .role(RoleName.EMPLOYEE)
                        .userStatus(UserStatus.ACTIVE)
                        .build();
                userOpt = Optional.of(userRepository.save(newUser));
            }

            // 2. Resolve Department
            String deptCode = record.get("department_code");
            Optional<Department> deptOpt = departmentRepository.findByDepartmentCode(deptCode);
            if (deptOpt.isEmpty()) {
                deptOpt = departmentRepository.findByDepartmentName(deptCode);
            }
            if (deptOpt.isEmpty()) {
                log.warn("Department '{}' not found for employee {}. Skipping.", deptCode, employeeCode);
                continue;
            }

            // 3. Resolve Designation
            String desigCode = record.get("designation_code");
            Optional<Designation> desigOpt = designationRepository.findByDesignationCode(desigCode);
            if (desigOpt.isEmpty()) {
                desigOpt = designationRepository.findByDesignationName(desigCode);
            }
            if (desigOpt.isEmpty()) {
                log.warn("Designation '{}' not found for employee {}. Skipping.", desigCode, employeeCode);
                continue;
            }

            // 4. Resolve JobTitle
            String jobTitleStr = record.get("job_title");
            Optional<JobTitle> jobTitleOpt = jobTitleRepository.findByJobTitle(jobTitleStr);
            if (jobTitleOpt.isEmpty()) {
                log.info("Job title '{}' not found in master data. Creating dynamically for designation '{}'.", jobTitleStr, desigCode);
                JobTitle newJobTitle = JobTitle.builder()
                        .jobTitleCode("JT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                        .jobTitle(jobTitleStr)
                        .designation(desigOpt.get())
                        .active(true)
                        .build();
                jobTitleOpt = Optional.of(jobTitleRepository.save(newJobTitle));
            }

            Employee employee = Employee.builder()
                    .employeeCode(employeeCode)
                    .firstName(record.get("first_name"))
                    .lastName(record.get("last_name"))
                    .email(email)
                    .phoneNumber(record.get("phone_number"))
                    .gender(Gender.valueOf(record.get("gender")))
                    .dateOfBirth(LocalDate.parse(record.get("dob")))
                    .dateOfJoining(LocalDate.parse(record.get("doj")))
                    .employmentType(EmploymentType.valueOf(record.get("employment_type")))
                    .department(deptOpt.get())
                    .designation(desigOpt.get())
                    .jobTitle(jobTitleOpt.get())
                    .user(userOpt.get())
                    .roleName(userOpt.get().getRole() != null ? userOpt.get().getRole() : RoleName.EMPLOYEE)
                    .active(true)
                    .build();

            employeeRepository.save(employee);
            log.info("Seeded employee: {}", employeeCode);
        }
    }
}
