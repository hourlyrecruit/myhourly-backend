package com.my_hourly.Form_16A.repository;

import com.my_hourly.Form_16A.entity.Form16Challan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Form16ChallanRepository
        extends JpaRepository<Form16Challan, Long> {

    // =========================================================
    // FIND ALL CHALLANS OF FORM16
    // =========================================================

    List<Form16Challan> findByForm16IdOrderBySerialNumberAsc(
            Long form16Id
    );


    // =========================================================
    // FIND SINGLE CHALLAN
    // =========================================================

    Optional<Form16Challan> findByIdAndForm16Id(
            Long id,
            Long form16Id
    );


    // =========================================================
    // CHECK EXISTENCE
    // =========================================================

    boolean existsByIdAndForm16Id(
            Long id,
            Long form16Id
    );


    // =========================================================
    // DELETE ALL CHALLANS OF FORM16
    // =========================================================

    void deleteByForm16Id(
            Long form16Id
    );


    // =========================================================
    // GET LAST SERIAL NUMBER
    // =========================================================

    Optional<Form16Challan>
    findTopByForm16IdOrderBySerialNumberDesc(
            Long form16Id
    );
}