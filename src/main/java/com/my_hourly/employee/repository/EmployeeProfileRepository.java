package com.my_hourly.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.employee.entity.EmployeeProfile;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

   
    Optional<EmployeeProfile> findByEmployeeCode(String employeeCode);

    
    boolean existsByEmployeeCode(String employeeCode);

    

    void deleteByEmployeeCode(String employeeCode);

}