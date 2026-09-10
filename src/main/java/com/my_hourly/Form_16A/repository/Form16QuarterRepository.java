package com.my_hourly.Form_16A.repository;

import com.my_hourly.Form_16A.entity.Form16Quarter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface Form16QuarterRepository
        extends JpaRepository<Form16Quarter, Long> {

    // =========================================================
    // FIND BY FORM16 ID
    // =========================================================

    Optional<Form16Quarter> findByForm16Id(
            Long form16Id
    );


    // =========================================================
    // CHECK FORM16 QUARTER EXISTS
    // =========================================================

    boolean existsByForm16Id(
            Long form16Id
    );


    // =========================================================
    // DELETE BY FORM16 ID
    // =========================================================

    void deleteByForm16Id(
            Long form16Id
    );


    // =========================================================
    // CHECK Q1 RECEIPT
    // =========================================================

    boolean existsByQ1ReceiptNumber(
            String q1ReceiptNumber
    );


    // =========================================================
    // CHECK Q2 RECEIPT
    // =========================================================

    boolean existsByQ2ReceiptNumber(
            String q2ReceiptNumber
    );


    // =========================================================
    // CHECK Q3 RECEIPT
    // =========================================================

    boolean existsByQ3ReceiptNumber(
            String q3ReceiptNumber
    );


    // =========================================================
    // CHECK Q4 RECEIPT
    // =========================================================

    boolean existsByQ4ReceiptNumber(
            String q4ReceiptNumber
    );


    // =========================================================
    // GET MAX BOOK ADJUSTMENT SERIAL NUMBER
    // =========================================================

    @Query("""
            SELECT COALESCE(
                MAX(q.bookAdjustmentSerialNumber),
                0
            )
            FROM Form16Quarter q
            """)
    Integer findMaxBookAdjustmentSerialNumber();
}