package com.my_hourly.seed.LeaveBalanceSeeder;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveBalanceSeeder {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Transactional
    public void seed() {
        if (leaveBalanceRepository.count() > 0) {
            log.info("Leave balances already seeded. Skipping...");
            return;
        }

        List<Employee> employees = employeeRepository.findByActiveTrue();
        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();
        int currentYear = java.time.LocalDate.now().getYear();

        for (Employee employee : employees) {
            for (LeaveType leaveType : leaveTypes) {
                if (!leaveBalanceRepository.existsByEmployeeAndLeaveTypeAndYear(employee, leaveType, currentYear)) {
                    LeaveBalance balance = LeaveBalance.builder()
                            .employee(employee)
                            .leaveType(leaveType)
                            .year(currentYear)
                            .allocatedLeaves(leaveType.getAllocatedDays())
                            .usedLeaves(0)
                            .expiredLeaves(0)
                            .remainingLeaves(leaveType.getAllocatedDays())
                            .build();
                    leaveBalanceRepository.save(balance);
                }
            }
            log.info("Seeded leave balances for employee: {}", employee.getEmployeeCode());
        }
    }
}
