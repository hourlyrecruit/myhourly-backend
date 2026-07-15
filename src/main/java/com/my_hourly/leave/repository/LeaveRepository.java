package com.my_hourly.leave.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.leave.entity.Leave;
import com.my_hourly.leave.enums.LeaveStatus;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // =====================================================
    // Employee - View Own Leave History
    // =====================================================

    List<Leave> findByEmployeeCode(String employeeCode);

    // =====================================================
    // Manager / HR - View Leaves By Status
    // =====================================================

    List<Leave> findByStatus(LeaveStatus status);

    // =====================================================
    // Employee - View Leave By Status
    // =====================================================

    List<Leave> findByEmployeeCodeAndStatus(
            String employeeCode,
            LeaveStatus status);

    // =====================================================
    // Check Duplicate Leave Application
    // =====================================================

    boolean existsByEmployeeCodeAndFromDateAndToDate(
            String employeeCode,
            LocalDate fromDate,
            LocalDate toDate);

    // =====================================================
    // Current Month Approved Leaves
    // =====================================================

    List<Leave> findByEmployeeCodeAndStatusAndFromDateBetween(
            String employeeCode,
            LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate);

}