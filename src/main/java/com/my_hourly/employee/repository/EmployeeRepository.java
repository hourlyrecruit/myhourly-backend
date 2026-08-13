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

    List<Employee> findByActiveTrueOrderByFirstNameAsc();

    Optional<Employee> findByUser(User user);

    List<Employee> findByActiveTrue();

    List<Employee> findByActiveTrueAndRoleNameInOrderByFirstNameAsc(List<RoleName> manager);

    /**
     * Active employees whose birthday falls on the given day.
     * Used by the daily birthday scheduler (avoids loading every employee
     * and filtering in memory).
     */
    @Query("""
            SELECT e FROM Employee e
            WHERE e.active = true
              AND e.dateOfBirth IS NOT NULL
              AND MONTH(e.dateOfBirth) = :month
              AND DAY(e.dateOfBirth) = :day
            """)
    List<Employee> findActiveEmployeesWithBirthday(
            @Param("month") int month,
            @Param("day") int day
    );

    /**
     * Active employees whose work anniversary (date of joining) falls on the
     * given day. Used by the daily work-anniversary scheduler.
     */
    @Query("""
            SELECT e FROM Employee e
            WHERE e.active = true
              AND e.dateOfJoining IS NOT NULL
              AND MONTH(e.dateOfJoining) = :month
              AND DAY(e.dateOfJoining) = :day
            """)
    List<Employee> findActiveEmployeesWithWorkAnniversary(
            @Param("month") int month,
            @Param("day") int day
    );


    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

//    ==========================

    List<Employee> findByIdIn(List<Long> ids);

    List<Employee> findByUserIdIn(List<Long> userIds);


}