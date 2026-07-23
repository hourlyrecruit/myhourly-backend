package com.my_hourly.seed.LeaveRequestSeeder;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
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
public class LeaveRequestSeeder {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (leaveRequestRepository.count() > 0) {
            log.info("Leave requests already seeded. Skipping...");
            return;
        }

        List<Map<String, String>> records = csvReader.readCsv("seed/leave_requests.csv");
        for (Map<String, String> record : records) {
            String empCode = record.get("employee_code");
            String leaveTypeName = record.get("leave_type_name");

            Optional<Employee> empOpt = employeeRepository.findAll().stream()
                    .filter(e -> e.getEmployeeCode().equals(empCode))
                    .findFirst();

            Optional<LeaveType> leaveTypeOpt = leaveTypeRepository.findByNameIgnoreCase(leaveTypeName);

            if (empOpt.isEmpty()) {
                log.warn("Employee '{}' not found. Skipping leave request.", empCode);
                continue;
            }
            if (leaveTypeOpt.isEmpty()) {
                log.warn("Leave type '{}' not found. Skipping leave request.", leaveTypeName);
                continue;
            }

            LeaveRequest request = LeaveRequest.builder()
                    .employee(empOpt.get())
                    .leaveType(leaveTypeOpt.get())
                    .startDate(LocalDate.parse(record.get("start_date")))
                    .endDate(LocalDate.parse(record.get("end_date")))
                    .totalDays(Integer.parseInt(record.get("total_days")))
                    .reason(record.get("reason"))
                    .status(LeaveStatus.valueOf(record.get("status")))
                    .build();

            leaveRequestRepository.save(request);
            log.info("Seeded leave request for employee: {}", empCode);
        }
    }
}
