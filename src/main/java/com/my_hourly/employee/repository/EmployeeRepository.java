package com.my_hourly.employee.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    Optional<Employee> findTopByOrderByEmployeeCodeDesc();

    // Numeric-safe lookup for the highest employee code (e.g. EMP001, EMP002 ... EMP999, EMP1000)
    @Query(value = "SELECT * FROM employees " +
            "ORDER BY CAST(SUBSTRING(employee_code, 4) AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Employee> findEmployeeWithHighestCode();

    List<Employee> findByActiveTrueOrderByFirstNameAsc();

    Optional<Employee> findByUser(User user);

    List<Employee> findByActiveTrue();

    List<Employee> findByActiveTrueAndRoleNameInOrderByFirstNameAsc(List<RoleName> manager);

    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

//    ==========================

    List<Employee> findByIdIn(List<Long> ids);

    List<Employee> findByUserIdIn(List<Long> userIds);

    @Query("SELECT e FROM Employee e WHERE e.active = true " +
            "AND MONTH(e.dateOfBirth) = MONTH(CURRENT_DATE) " +
            "AND DAY(e.dateOfBirth) = DAY(CURRENT_DATE)")
    List<Employee> findEmployeesWithBirthdayToday();

}