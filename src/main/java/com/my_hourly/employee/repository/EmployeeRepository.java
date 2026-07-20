package com.my_hourly.employee.repository;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);
    Optional<Employee> findByUserId(Long userId);

    boolean existsByEmployeeCode(String employeeCode);

    Optional<Employee> findTopByOrderByEmployeeCodeDesc();

    Page<Employee> findByEmployeeCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String employeeCode,
            String firstName,
            String lastName,
            String email,
            Pageable pageable
    );

    List<Employee> findByActiveTrueOrderByFirstNameAsc();

    Optional<Employee> findByUser(User user);

    boolean existsByUser(User user);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Employee> findByActiveTrueAndRoleNameOrderByFirstNameAsc(RoleName role);
}