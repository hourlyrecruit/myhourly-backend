package com.my_hourly.leave.service;

public interface LeaveAllocationService {

    /**
     * Allocate annual leave balance for a specific employee across all active leave types.
     * Called when a new employee is onboarded mid-year.
     */
    void allocateForEmployee(Long employeeId);

    /**
     * Allocate annual leave balance for all active employees.
     * Typically called on Jan 1st by the yearly scheduler.
     */
    void allocateForAllEmployees();

    /**
     * Triggered by the yearly scheduler (Jan 1).
     * Creates one LeaveBalance record per active employee per active leave type for the new year.
     */
    void allocateYearlyLeaves();

}