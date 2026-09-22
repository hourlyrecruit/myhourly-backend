package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16EmployerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Form16EmployerMasterRepository
        extends JpaRepository<Form16EmployerMaster, Long> {


    // =========================================================
    // ACTIVE EMPLOYER
    // =========================================================

    Optional<Form16EmployerMaster> findByActiveTrue();


    // =========================================================
    // ALL EMPLOYERS
    // =========================================================

    List<Form16EmployerMaster>
    findAllByOrderByIdDesc();


    // =========================================================
    // CHECK ACTIVE EMPLOYER
    // =========================================================

    boolean existsByActiveTrue();


    // =========================================================
    // PAN
    // =========================================================

    boolean existsByDeductorPan(
            String deductorPan
    );


    // =========================================================
    // TAN
    // =========================================================

    boolean existsByDeductorTan(
            String deductorTan
    );
}
