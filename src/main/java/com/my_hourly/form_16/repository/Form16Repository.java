package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Form16Repository
        extends JpaRepository<Form16, Long> {


    // =========================================================
    // FIND BY EMPLOYEE + ASSESSMENT YEAR
    // =========================================================

    Optional<Form16> findByEmployeeIdAndAssessmentYear(
            Long employeeId,
            String assessmentYear
    );


    // =========================================================
    // CHECK DUPLICATE EMPLOYEE + ASSESSMENT YEAR
    // =========================================================

    boolean existsByEmployeeIdAndAssessmentYear(
            Long employeeId,
            String assessmentYear
    );


    // =========================================================
    // CHECK CERTIFICATE NUMBER
    // =========================================================

    boolean existsByCertificateNo(
            String certificateNo
    );


    // =========================================================
    // FIND ALL FORM16 RECORDS OF EMPLOYEE
    // =========================================================

    List<Form16> findAllByEmployeeId(
            Long employeeId
    );


    // =========================================================
    // FIRST FORM16 OF EMPLOYEE
    // USED TO GET SAVED EMPLOYEE ADDRESS
    // =========================================================

    Optional<Form16> findFirstByEmployeeIdOrderByIdAsc(
            Long employeeId
    );
}