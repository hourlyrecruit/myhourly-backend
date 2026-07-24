package com.my_hourly.seed.attendance;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.EmployeeStatus;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceSeeder {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (attendanceRepository.count() > 0) {
            log.info("Attendance records already seeded. Skipping...");
            return;
        }

        List<Map<String, String>> records = csvReader.readCsv("seed/attendance.csv");
        for (Map<String, String> record : records) {
            String empCode = record.get("employee_code");

            Optional<Employee> empOpt = employeeRepository.findAll().stream()
                    .filter(e -> e.getEmployeeCode().equals(empCode))
                    .findFirst();

            if (empOpt.isEmpty()) {
                log.warn("Employee '{}' not found for attendance record. Skipping.", empCode);
                continue;
            }

            Employee employee = empOpt.get();
            LocalDate attendanceDate = LocalDate.parse(record.get("attendance_date"));

            if (attendanceRepository.existsByEmployeeAndAttendanceDate(employee, attendanceDate)) {
                log.info("Attendance for {} on {} already exists. Skipping.", empCode, attendanceDate);
                continue;
            }

            Attendance attendance = Attendance.builder()
                    .employee(employee)
                    .attendanceDate(attendanceDate)
                    .checkInTime(LocalDateTime.parse(record.get("check_in_time")))
                    .checkOutTime(LocalDateTime.parse(record.get("check_out_time")))
                    .workingMinutes(Integer.parseInt(record.get("working_minutes")))
                    .totalBreakMinutes(Integer.parseInt(record.get("total_break_minutes")))
                    .attendanceStatus(AttendanceStatus.valueOf(record.get("attendance_status")))
                    .employeeStatus(EmployeeStatus.valueOf(record.get("employee_status")))
                    .lateMinutes(0)
                    .earlyExitMinutes(0)
                    .overtimeMinutes(0)
                    .build();

            attendanceRepository.save(attendance);
            log.info("Seeded attendance for employee {} on {}", empCode, attendanceDate);
        }
    }
}

