package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveAllocationService;
import com.my_hourly.leave.service.LeaveTransactionService;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LeaveAllocationServiceImpl implements LeaveAllocationService {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTransactionService leaveTransactionService;
    private final LeaveSettingsService leaveSettingsService;

    @Override
    public void allocateForEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found.",
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        for (LeaveType leaveType : leaveTypes) {
            allocateAnnualBalance(employee, leaveType);
        }
    }

    @Override
    public void allocateForAllEmployees() {

        List<Employee> employees = employeeRepository.findByActiveTrue();

        for (Employee employee : employees) {
            allocateForEmployee(employee.getId());
        }
    }

    @Override
    @Transactional
    public void allocateYearlyLeaves() {

        List<Employee> employees = employeeRepository.findByActiveTrue();

        for (Employee employee : employees) {
            allocateYearlyLeaveForEmployee(employee);
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void allocateYearlyLeaveForEmployee(Employee employee) {

        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        for (LeaveType leaveType : leaveTypes) {
            allocateAnnualBalance(employee, leaveType);
        }
    }

    /**
     * Creates a single annual LeaveBalance record for the given employee and leave type
     * if one does not already exist for the current year.
     */
    private void allocateAnnualBalance(Employee employee, LeaveType leaveType) {

        int year = LocalDate.now().getYear();

        boolean exists = leaveBalanceRepository
                .existsByEmployeeAndLeaveTypeAndYear(
                        employee,
                        leaveType,
                        year
                );

        if (exists) {
            log.debug("Annual leave balance already exists for employee {} leaveType {} year {}",
                    employee.getId(), leaveType.getId(), year);
            return;
        }

        int allocatedDays = leaveType.getAllocatedDays();
        if (Boolean.TRUE.equals(leaveType.getPaid())) {
            try {
                LeaveSettings settings = leaveSettingsService.getSettings();
                if (settings.getAnnualPaidLeave() != null) {
                    allocatedDays = settings.getAnnualPaidLeave();
                }
            } catch (Exception e) {
                log.warn("Could not retrieve LeaveSettings, falling back to LeaveType allocatedDays", e);
            }
        }

        LeaveBalance leaveBalance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(year)
                .allocatedLeaves(allocatedDays)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(allocatedDays)
                .build();

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createAllocationTransaction(
                leaveBalance,
                allocatedDays
        );

        log.info("Allocated {} days of {} for employee {} for year {}",
                allocatedDays, leaveType.getName(),
                employee.getId(), year);
    }
}