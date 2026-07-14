package com.my_hourly.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.employee.entity.EmployeeAddress;

@Repository
public interface EmployeeAddressRepository extends JpaRepository<EmployeeAddress, Long> {

   

    Optional<EmployeeAddress> findByEmployeeCode(String employeeCode);

   

    boolean existsByEmployeeCode(String employeeCode);

    

    void deleteByEmployeeCode(String employeeCode);

}