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
    // Get All Leaves By Employee Code
    // =====================================================

    List<Leave> findByEmployeeCode(String employeeCode);

    // =====================================================
    // Get All Leaves By Status
    // =====================================================

    List<Leave> findByStatus(LeaveStatus status);

    // =====================================================
    // Get Employee Leaves By Status
    // =====================================================

    List<Leave> findByEmployeeCodeAndStatus(
            String employeeCode,
            LeaveStatus status);

    // =====================================================
    // Count Approved Leaves
    // =====================================================

    long countByEmployeeCodeAndStatus(
            String employeeCode,
            LeaveStatus status);

    // =====================================================
    // Check Duplicate Leave Application
    // =====================================================

    boolean existsByEmployeeCodeAndFromDateAndToDate(
            String employeeCode,
            LocalDate fromDate,
            LocalDate toDate);

}