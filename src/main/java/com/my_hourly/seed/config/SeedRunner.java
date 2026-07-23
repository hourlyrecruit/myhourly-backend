package com.my_hourly.seed.config;

import com.my_hourly.seed.LeaveBalanceSeeder.LeaveBalanceSeeder;
import com.my_hourly.seed.LeaveRequestSeeder.LeaveRequestSeeder;
import com.my_hourly.seed.LeaveTypeSeeder.LeaveTypeSeeder;
import com.my_hourly.seed.attendance.AttendanceSeeder;
import com.my_hourly.seed.employee.EmployeeSeeder;
import com.my_hourly.seed.employee.UserSeeder;
import com.my_hourly.seed.holiday.HolidaySeeder;
import com.my_hourly.seed.master.DepartmentSeeder;
import com.my_hourly.seed.master.DesignationSeeder;
import com.my_hourly.seed.master.JobTitleSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(2) // Runs after DataInitializer (which is @Order(1) by default)
@RequiredArgsConstructor
public class SeedRunner implements ApplicationRunner {

    // Master data
    private final DepartmentSeeder departmentSeeder;
    private final DesignationSeeder designationSeeder;
    private final JobTitleSeeder jobTitleSeeder;
    private final HolidaySeeder holidaySeeder;

    // Users and Employees
    private final UserSeeder userSeeder;
    private final EmployeeSeeder employeeSeeder;

    // Leave
    private final LeaveTypeSeeder leaveTypeSeeder;
    private final LeaveBalanceSeeder leaveBalanceSeeder;
    private final LeaveRequestSeeder leaveRequestSeeder;

    // Attendance
    private final AttendanceSeeder attendanceSeeder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========== [SeedRunner] Starting data seeding ==========");

        // Step 1: Master data (no dependencies)
        departmentSeeder.seed();
        designationSeeder.seed();
        jobTitleSeeder.seed();
        holidaySeeder.seed();

        // Step 2: Users and Employees (depends on master data)
        userSeeder.seed();
        employeeSeeder.seed();

        // Step 3: Leave types and balances (depends on employees)
        leaveTypeSeeder.seed();
        leaveBalanceSeeder.seed();
        leaveRequestSeeder.seed();

        // Step 4: Attendance (depends on employees)
        attendanceSeeder.seed();

        log.info("========== [SeedRunner] Data seeding complete ==========");
    }
}

