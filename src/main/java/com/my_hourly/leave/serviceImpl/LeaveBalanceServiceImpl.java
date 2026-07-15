package com.my_hourly.leave.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.enums.EmploymentType;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.response.LeaveBalanceResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.exception.LeaveBalanceException;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.service.LeaveBalanceService;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // =====================================================
    // Employee - View Current Month Leave Balance
    // =====================================================

    @Override
    public LeaveBalanceResponse getLeaveBalance(String employeeCode) {

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        // Check Employee Exists
        employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new LeaveBalanceException("Employee Not Found"));

        LeaveBalance leaveBalance = leaveBalanceRepository
                .findByEmployeeCodeAndMonthAndYear(
                        employeeCode,
                        month,
                        year)
                .orElseThrow(() ->
                        new LeaveBalanceException("Leave Balance Not Found"));

        return mapToResponse(leaveBalance);
    }

    // =====================================================
    // Entity -> Response
    // =====================================================

    private LeaveBalanceResponse mapToResponse(
            LeaveBalance leaveBalance) {

        LeaveBalanceResponse response =
                new LeaveBalanceResponse();

        response.setEmployeeCode(
                leaveBalance.getEmployeeCode());

        response.setEmployeeFirstName(
                leaveBalance.getEmployeeFirstName());

        response.setEmployeeLastName(
                leaveBalance.getEmployeeLastName());

        response.setYear(
                leaveBalance.getYear());

        response.setMonth(
                leaveBalance.getMonth());

        response.setMonthlyLeave(
                leaveBalance.getMonthlyLeave());

        response.setUsedLeave(
                leaveBalance.getUsedLeave());

        response.setAvailableLeave(
                leaveBalance.getAvailableLeave());

        response.setExpiredLeave(
                leaveBalance.getExpiredLeave());

        return response;
    }

    // =====================================================
    // Monthly Leave Reset
    // Runs on 1st Day of Every Month
    // =====================================================

    @Override
    public void monthlyLeaveReset() {

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {

            LeaveBalance leaveBalance =
                    leaveBalanceRepository
                    .findByEmployeeCodeAndMonthAndYear(
                            employee.getEmployeeCode(),
                            month,
                            year)
                    .orElse(new LeaveBalance());

            leaveBalance.setEmployee(employee);
            leaveBalance.setEmployeeCode(employee.getEmployeeCode());
            leaveBalance.setEmployeeFirstName(employee.getFirstName());
            leaveBalance.setEmployeeLastName(employee.getLastName());

            leaveBalance.setMonth(month);
            leaveBalance.setYear(year);

            // =============================================
            // Expire Remaining Leave (No Carry Forward)
            // =============================================

            int expiredLeave = leaveBalance.getAvailableLeave() == null
                    ? 0
                    : leaveBalance.getAvailableLeave();

            leaveBalance.setExpiredLeave(expiredLeave);

            // =============================================
            // Reset Monthly Leave
            // =============================================

            leaveBalance.setUsedLeave(0);

            if (employee.getEmploymentType() == EmploymentType.FULL_TIME) {

                leaveBalance.setMonthlyLeave(2);
                leaveBalance.setAvailableLeave(2);

            } else {

                // INTERN

                leaveBalance.setMonthlyLeave(0);
                leaveBalance.setAvailableLeave(0);

            }

            leaveBalanceRepository.save(leaveBalance);
        }

        System.out.println("Monthly Leave Reset Completed Successfully.");
    }

}