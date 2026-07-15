package com.my_hourly.config;

import com.my_hourly.authentication.entity.*;
import com.my_hourly.authentication.repository.*;
import com.my_hourly.authentication.entity.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSuperAdmin();
        seedManager();
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

}
