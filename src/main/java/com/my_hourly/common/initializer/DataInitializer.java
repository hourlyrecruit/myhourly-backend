package com.my_hourly.common.initializer;

import com.my_hourly.authentication.entity.*;
import com.my_hourly.authentication.repository.*;
import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.master.api.request.CreateDepartmentRequest;
import com.my_hourly.master.api.request.CreateDesignationRequest;
import com.my_hourly.master.api.request.CreateJobTitleRequest;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.master.service.DepartmentService;
import com.my_hourly.master.service.DesignationService;
import com.my_hourly.master.service.JobTitleService;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import com.my_hourly.settings.company.entity.CompanySettings;
import com.my_hourly.settings.attendance.repository.AttendanceSettingsRepository;
import com.my_hourly.settings.company.repository.CompanySettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;


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
    private final CompanySettingsRepository companySettingsRepository;
    private final AttendanceSettingsRepository attendanceSettingsRepository;
   // private final SuperAdminProperties superAdminProperties;

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
        seedCompanySettings();
        seedAttendanceSettings();
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
                //.username(superAdminProperties.getUsername();)
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

    private void seedCompanySettings() {

        if (companySettingsRepository.count() > 0) {
            log.info("[DataInitializer] Company Settings already exist - skipping.");
            return;
        }

        CompanySettings settings = CompanySettings.builder()
                .companyName("MyHourly")
                .companyCode("MHR")
                .email(superAdminEmail)
                .phoneNumber("9876543210")
                .timeZone("Asia/Kolkata")
                .currency("INR")
                .workingDaysPerWeek(5)
                .active(true)
                .build();

        companySettingsRepository.save(settings);

        log.info("[DataInitializer] Company Settings created successfully.");
    }

    private void seedAttendanceSettings() {

        if (attendanceSettingsRepository.count() > 0) {
            log.info("[DataInitializer] Attendance Settings already exist - skipping.");
            return;
        }

        AttendanceSettings settings = AttendanceSettings.builder()
                .officeStartTime(LocalTime.of(9, 30))
                .officeEndTime(LocalTime.of(18, 30))
                .gracePeriodMinutes(45)
                .minimumWorkingMinutes(480)
                .halfDayWorkingMinutes(240)
                .checkoutCutoffMinutes(180)
                .overtimeEnabled(false)
                .attendanceRegularizationEnabled(true)
                .multipleBreaksAllowed(true)
                .maximumBreakMinutes(60)
                .active(true)
                .build();

        attendanceSettingsRepository.save(settings);

        log.info("[DataInitializer] Attendance Settings created successfully.");
    }


}
