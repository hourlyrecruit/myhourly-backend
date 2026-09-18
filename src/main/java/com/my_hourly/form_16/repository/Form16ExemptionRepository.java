package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16Exemption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Form16ExemptionRepository
        extends JpaRepository<Form16Exemption, Long> {

    Optional<Form16Exemption> findByForm16Id(
            Long form16Id
    );

    boolean existsByForm16Id(
            Long form16Id
    );

    void deleteByForm16Id(
            Long form16Id
    );
}