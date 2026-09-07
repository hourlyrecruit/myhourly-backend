package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Form16VerificationRepository
        extends JpaRepository<Form16Verification, Long> {

    // =========================================================
    // FIND VERIFICATION BY FORM 16 ID
    // =========================================================

    Optional<Form16Verification> findByForm16Id(Long form16Id);


    // =========================================================
    // CHECK WHETHER VERIFICATION EXISTS
    // FOR PARTICULAR FORM 16
    // =========================================================

    boolean existsByForm16Id(Long form16Id);


    // =========================================================
    // DELETE VERIFICATION BY FORM 16 ID
    // =========================================================

    void deleteByForm16Id(Long form16Id);
}