package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveAllocationType;
import com.my_hourly.leave.enums.MonthType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveAllocationService;
import com.my_hourly.leave.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveAllocationServiceImpl implements LeaveAllocationService {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTransactionService leaveTransactionService;

    @Override
    public void allocateForEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found.",
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        for (LeaveType leaveType : leaveTypes) {

            switch (leaveType.getAllocationType()) {

                case MONTHLY ->
                        allocateMonthlyBalance(employee, leaveType);

                case YEARLY ->
                        allocateYearlyBalance(employee, leaveType);
            }
        }
    }

    @Override
    public void allocateForAllEmployees() {

    }

    private void allocateMonthlyBalance(Employee employee,
                                        LeaveType leaveType) {

        LocalDate today = LocalDate.now();

        int year = today.getYear();

        MonthType month = MonthType.valueOf(today.getMonth().name());

        boolean exists = leaveBalanceRepository
                .existsByEmployeeAndLeaveTypeAndYearAndMonth(
                        employee,
                        leaveType,
                        year,
                        month
                );

        if (exists) {
            return;
        }

        LeaveBalance leaveBalance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(year)
                .month(month)
                .allocatedLeaves(leaveType.getAllocatedDays())
                .carriedForwardLeaves(0)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(leaveType.getAllocatedDays())
                .build();

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createAllocationTransaction(
                leaveBalance,
                leaveType.getAllocatedDays()
        );
    }


    private void allocateYearlyBalance(Employee employee,
                                       LeaveType leaveType) {

        int year = LocalDate.now().getYear();

        boolean exists = leaveBalanceRepository
                .existsByEmployeeAndLeaveTypeAndYearAndMonth(
                        employee,
                        leaveType,
                        year,
                        null
                );

        if (exists) {
            return;
        }

        LeaveBalance leaveBalance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(year)
                .month(null)
                .allocatedLeaves(leaveType.getAllocatedDays())
                .carriedForwardLeaves(0)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(leaveType.getAllocatedDays())
                .build();

        leaveBalanceRepository.save(leaveBalance);

        leaveTransactionService.createAllocationTransaction(
                leaveBalance,
                leaveType.getAllocatedDays()
        );
    }

    @Override
    public void allocateMonthlyLeaves() {

    }

    @Override
    public void allocateYearlyLeaves() {

    }


}