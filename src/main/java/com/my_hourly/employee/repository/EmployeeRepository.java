package com.my_hourly.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    
    Optional<Employee> findByEmployeeCode(String employeeCode);

   
    Optional<Employee> findByEmail(String email);

    
    Optional<Employee> findByMobileNumber(String mobileNumber);

    
    boolean existsByEmployeeCode(String employeeCode);

    
    boolean existsByEmail(String email);

   
    boolean existsByMobileNumber(String mobileNumber);

}