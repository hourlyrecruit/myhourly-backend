package com.my_hourly.leave.service;

public interface LeaveAllocationService {

    void allocateForEmployee(Long employeeId);

    void allocateForAllEmployees();

    void allocateMonthlyLeaves();

    void allocateYearlyLeaves();

    void expireLeaves();
}