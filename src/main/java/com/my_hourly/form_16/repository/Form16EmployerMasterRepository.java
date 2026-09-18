package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16EmployerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Form16EmployerMasterRepository
        extends JpaRepository<Form16EmployerMaster, Long> {

    Optional<Form16EmployerMaster> findByActiveTrue();

    List<Form16EmployerMaster> findAllByOrderByIdDesc();

    boolean existsByActiveTrue();
}