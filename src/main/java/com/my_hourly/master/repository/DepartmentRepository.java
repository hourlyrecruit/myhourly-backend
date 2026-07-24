package com.my_hourly.master.repository;

import com.my_hourly.master.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentCode(String departmentCode);

    Optional<Department> findTopByOrderByDepartmentCodeDesc();

    boolean existsByDepartmentName(
            String departmentName
    );

    Page<Department> findByDepartmentNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    List<Department> findByActiveTrueOrderByDepartmentNameAsc();

    Optional<Department> findByDepartmentName(String departmentName);

}
