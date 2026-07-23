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
import java.util.Optional;

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

    @Override
    @Transactional
    public void reallocateForLeaveType(Long leaveTypeId) {

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave Type id: " + leaveTypeId,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        List<Employee> employees = employeeRepository.findByActiveTrue();

        for (Employee employee : employees) {
            allocateAnnualBalance(employee, leaveType);
        }

        log.info("Reallocated leave type {} ({}) across {} employees",
                leaveType.getId(), leaveType.getName(), employees.size());
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
     * Creates a new annual LeaveBalance for the given employee/leaveType/year if none exists,
     * or adjusts the existing one (allocatedLeaves + remainingLeaves) by the delta if the
     * LeaveType's configured days have changed since it was last allocated.
     */
    private void allocateAnnualBalance(Employee employee, LeaveType leaveType) {

        int year = LocalDate.now().getYear();
        int allocatedDays = resolveAllocatedDays(leaveType);

        Optional<LeaveBalance> existingOpt = leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYear(employee, leaveType, year);

        if (existingOpt.isPresent()) {
            adjustExistingBalance(existingOpt.get(), allocatedDays);
            return;
        }

        LocalDate dateOfJoining = employee.getDateOfJoining();

        int finalAllocatedDays = allocatedDays;

        if (dateOfJoining.getYear() == year) {
            int monthsRemaining = 13 - dateOfJoining.getMonthValue(); // joining month through December, inclusive
            finalAllocatedDays = (allocatedDays * monthsRemaining) / 12;
        }

        LeaveBalance leaveBalance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(year)
                .allocatedLeaves(finalAllocatedDays)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(finalAllocatedDays)
                .build();

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createAllocationTransaction(
                leaveBalance,
                finalAllocatedDays
        );

        log.info("Allocated {} days of {} for employee {} for year {}",
                finalAllocatedDays, leaveType.getName(),
                employee.getId(), year);
    }

    private int resolveAllocatedDays(LeaveType leaveType) {

        int allocatedDays = leaveType.getAllocatedDays();

        // LeaveSettings is only a fallback when the LeaveType itself doesn't specify a value —
        // never override a LeaveType-specific value with the global setting.
        if (Boolean.TRUE.equals(leaveType.getPaid()) && allocatedDays <= 0) {
            try {
                LeaveSettings settings = leaveSettingsService.getSettings();
                if (settings.getAnnualPaidLeave() != null) {
                    allocatedDays = settings.getAnnualPaidLeave();
                }
            } catch (Exception e) {
                log.warn("Could not retrieve LeaveSettings, falling back to LeaveType allocatedDays", e);
            }
        }
        return allocatedDays;
    }

    private void adjustExistingBalance(LeaveBalance leaveBalance, int newAllocatedDays) {

        int oldAllocatedDays = leaveBalance.getAllocatedLeaves();
        int diff = newAllocatedDays - oldAllocatedDays;

        if (diff == 0) {
            return;
        }

        int beforeRemaining = leaveBalance.getRemainingLeaves();
        int afterRemaining = beforeRemaining + diff;

        if (afterRemaining < 0) {
            log.warn("Reducing allocation would push remaining leaves negative for employee {} leaveType {} " +
                            "(remaining={}, diff={}). Clamping remaining to 0 — used leaves exceed new allocation.",
                    leaveBalance.getEmployee().getId(), leaveBalance.getLeaveType().getId(), beforeRemaining, diff);
            afterRemaining = 0;
        }

        leaveBalance.setAllocatedLeaves(newAllocatedDays);
        leaveBalance.setRemainingLeaves(afterRemaining);

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createAdjustmentTransaction(
                leaveBalance,
                diff,
                beforeRemaining,
                afterRemaining,
                "Leave type allocation updated from " + oldAllocatedDays + " to " + newAllocatedDays
        );

        log.info("Adjusted allocation for employee {} leaveType {}: {} -> {} (remaining {} -> {})",
                leaveBalance.getEmployee().getId(), leaveBalance.getLeaveType().getId(),
                oldAllocatedDays, newAllocatedDays, beforeRemaining, afterRemaining);
    }
}