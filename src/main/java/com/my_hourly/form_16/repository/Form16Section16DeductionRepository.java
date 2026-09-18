package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16Section16Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Form16Section16DeductionRepository
        extends JpaRepository<Form16Section16Deduction, Long> {

    Optional<Form16Section16Deduction> findByForm16Id(
            Long form16Id
    );

    boolean existsByForm16Id(
            Long form16Id
    );

    void deleteByForm16Id(
            Long form16Id
    );
}