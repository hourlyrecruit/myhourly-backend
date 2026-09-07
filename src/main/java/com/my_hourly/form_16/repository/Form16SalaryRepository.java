package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Form16SalaryRepository
        extends JpaRepository<Form16Salary, Long> {

    /**
     * Find salary details by Form16 ID.
     */
    Optional<Form16Salary> findByForm16Id(
            Long form16Id
    );

    /**
     * Check whether salary details
     * already exist for a Form16.
     */
    boolean existsByForm16Id(
            Long form16Id
    );

    /**
     * Delete salary details by Form16 ID.
     */
    void deleteByForm16Id(
            Long form16Id
    );
}