package com.my_hourly.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // =====================================================
    // Find Employee By Employee Code
    // =====================================================

    Optional<Employee> findByEmployeeCode(String employeeCode);

    // =====================================================
    // Find Employee By Office Email
    // =====================================================

    Optional<Employee> findByOfficeEmail(String officeEmail);

    // =====================================================
    // Check Employee Code Exists
    // =====================================================

    boolean existsByEmployeeCode(String employeeCode);

    // =====================================================
    // Check Office Email Exists
    // =====================================================

    boolean existsByOfficeEmail(String officeEmail);

    // =====================================================
    // Check Personal Email Exists
    // =====================================================

    boolean existsByPersonalEmail(String personalEmail);

    // =====================================================
    // Check Phone Number Exists
    // =====================================================

    boolean existsByPhoneNumber(String phoneNumber);

}