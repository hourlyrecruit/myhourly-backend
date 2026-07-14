package com.my_hourly.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.employee.entity.EmployeeContact;

@Repository
public interface EmployeeContactRepository extends JpaRepository<EmployeeContact, Long> {

    

    Optional<EmployeeContact> findByEmployeeCode(String employeeCode);

    

    boolean existsByEmployeeCode(String employeeCode);

   

    void deleteByEmployeeCode(String employeeCode);

}