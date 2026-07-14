package com.my_hourly.organization.repository;

import com.my_hourly.organization.entity.Department;
import com.my_hourly.organization.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface DesignationRepository extends JpaRepository<Designation, Long> {

    Optional<Designation> findTopByOrderByDesignationCodeDesc();

    Optional<Designation> findByDesignationCode(String designationCode);

    boolean existsByDesignationNameAndDepartment(
            String designationName,
            Department department
    );

    Page<Designation> findByDesignationNameContainingIgnoreCase(
            String designationName,
            Pageable pageable
    );

}