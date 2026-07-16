package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.AttendancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendancePolicyRepository extends JpaRepository<AttendancePolicy, Long> {

    Optional<AttendancePolicy> findByActiveTrue();

    Optional<AttendancePolicy> findByPolicyName(String policyName);

}