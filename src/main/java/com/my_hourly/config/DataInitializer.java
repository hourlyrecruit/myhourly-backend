package com.my_hourly.config;

import com.my_hourly.authentication.entity.*;
import com.my_hourly.authentication.repository.*;
import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.employee.entity.Gender;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.master.api.request.CreateDepartmentRequest;
import com.my_hourly.master.api.request.CreateDesignationRequest;
import com.my_hourly.master.api.request.CreateJobTitleRequest;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.master.service.DepartmentService;
import com.my_hourly.master.service.DesignationService;
import com.my_hourly.master.service.JobTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final JobTitleRepository jobTitleRepository;
    private final DepartmentService departmentService;
    private final DesignationService designationService;
    private final JobTitleService jobTitleService;
//    private final EmployeeService employeeService;
//    private final EmployeeRepository employeeRepository;

    @Value("${app.super-admin.username}")
    private String superAdminUsername;

    @Value("${app.super-admin.email}")
    private String superAdminEmail;

    @Value("${app.super-admin.password}")
    private String superAdminPassword;

    @Value("${app.manager.username}")
    private String managerUsername;

    @Value("${app.manager.email}")
    private String managerEmail;

    @Value("${app.manager.password}")
    private String managerPassword;

    //========================================================================
    @Value("${app.department.name}")
    private String departmentName;

    @Value("${app.department.description}")
    private String departmentDescription;
    //========================================================================
    //========================================================================
        @Value("${app.designation.name}")
        private String designationName;

        @Value("${app.department.id}")
        private Long departmentId;

        @Value("${app.designation.description}")
        private String designationDescription;
    //========================================================================

    //========================================================================
    @Value("${app.job.title}")
    private String jobTitle;

    @Value("${app.designation.id}")
    private Long designationId;
    //========================================================================






    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSuperAdmin();
        seedManager();
        seedDepartmentDesignationJobTitle();
    }


    private void seedManager() {

        if (userRepository.existsByUsername(managerUsername)) {
            log.info("[DataInitializer] MANAGER '{}' already exists — skipping.", managerUsername);
            return;
        }

        User manager = User.builder()
                .username(managerUsername)
                .email(managerEmail)
                .password(passwordEncoder.encode(managerPassword))
                .userStatus(UserStatus.ACTIVE)
                .role(RoleName.MANAGER)
                .build();

        userRepository.save(manager);

        log.info("[DataInitializer] ================================================");
        log.info("[DataInitializer]  SUPER_ADMIN created successfully!");
        log.info("[DataInitializer]  Username : {}", managerUsername);
        log.info("[DataInitializer]  Email    : {}", managerEmail);
        log.info("[DataInitializer]  Password : {}", managerPassword);
        log.info("[DataInitializer]  IMPORTANT: Change this password after first login!");
        log.info("[DataInitializer] ================================================");
    }


    private void seedSuperAdmin() {

        if (userRepository.existsByUsername(superAdminUsername)) {
            log.info("[DataInitializer] SUPER_ADMIN '{}' already exists — skipping.", superAdminUsername);
            return;
        }

        User superAdmin = User.builder()
                .username(superAdminUsername)
                .email(superAdminEmail)
                .password(passwordEncoder.encode(superAdminPassword))
                .userStatus(UserStatus.ACTIVE)
                .role(RoleName.SUPER_ADMIN)
                .build();

        userRepository.save(superAdmin);

        log.info("[DataInitializer] ================================================");
        log.info("[DataInitializer]  SUPER_ADMIN created successfully!");
        log.info("[DataInitializer]  Username : {}", superAdminUsername);
        log.info("[DataInitializer]  Email    : {}", superAdminEmail);
        log.info("[DataInitializer]  Password : {}", superAdminPassword);
        log.info("[DataInitializer]  IMPORTANT: Change this password after first login!");
        log.info("[DataInitializer] ================================================");
    }

    private String getDefaultDescription(RoleName roleName) {
        return switch (roleName) {
            case SUPER_ADMIN    -> "Full system access. Can manage all users and roles.";
            case HR_ADMIN       -> "Human Resources administrator. Manages employees, leaves, and attendance.";
            case MANAGER        -> "Team manager. Can approve timesheets and leave requests.";
            case EMPLOYEE       -> "Standard employee. Can log time, apply for leave, and view own data.";
            case PAYROLL_ADMIN  -> "Payroll administrator. Manages salary, deductions, and payroll runs.";
            case CLIENT         -> "Client user. Read-only access to project reports.";
        };
    }

    private void seedDepartmentDesignationJobTitle(){
        if (departmentRepository.existsByDepartmentName(departmentName)
                && designationRepository.existsByDesignationName(designationName)
                && jobTitleRepository.existsByJobTitle(jobTitle)) {
            log.info("[DataInitializer] departmentName,departmentName,jobTitle '{}' already exists — skipping.", departmentName);
            return;
        }

        CreateDepartmentRequest dept = new CreateDepartmentRequest();
        dept.setDepartmentName(departmentName);
        dept.setDescription(departmentDescription);
        departmentService.create(dept);

        CreateDesignationRequest desi = new CreateDesignationRequest();
        desi.setDesignationName(designationName);
        desi.setDepartmentId(departmentId);
        desi.setDescription(designationDescription);
        designationService.create(desi);

        CreateJobTitleRequest job = new CreateJobTitleRequest();
        job.setJobTitle(jobTitle);
        job.setDesignationId(designationId);
        jobTitleService.create(job);

    }


//    @Value("${default.employee.first-name}")
//    private String firstName;
//
//    @Value("${default.employee.last-name}")
//    private String lastName;
//
//    @Value("${default.employee.phone-number}")
//    private String phoneNumber;
//
//    @Value("${default.employee.gender}")
//    private Gender gender;
//
//    @Value("${default.employee.date-of-birth}")
//    private LocalDate dateOfBirth;
//
//    @Value("${default.employee.date-of-joining}")
//    private LocalDate dateOfJoining;
//
//    @Value("${default.employee.employment-type}")
//    private EmploymentType employmentType;
//
//    @Value("${default.employee.department-id}")
//    private Long departId;
//
//    @Value("${default.employee.designation-id}")
//    private Long desiId;
//
//    @Value("${default.employee.job-title-id}")
//    private Long jobId;
//
//    @Value("${default.employee.reporting-manager-id}")
//    private Long reportingManagerId;
//
//    private void seedManagerEmployee(){
//        if (employeeRepository.existsByPhoneNumber(phoneNumber)){
//            log.info("[DataInitializer] departmentName,departmentName,jobTitle '{}' already exists — skipping.", managerUsername);
//            return;
//        }
//
//        CreateEmployeeRequest request = new CreateEmployeeRequest();
//        request.setFirstName(firstName);
//        request.setLastName(lastName);
//        request.setPhoneNumber(phoneNumber);
//        request.setGender(gender);
//        request.setDateOfBirth(dateOfBirth);
//        request.setDateOfJoining(dateOfJoining);
//        request.setEmploymentType(employmentType);
//        request.setDepartmentId(departId);
//        request.setDesignationId(desiId);
//        request.setJobTitleId(jobId);
//        request.setReportingManagerId(reportingManagerId);
//
//        employeeService.create(request);
//    }



}
