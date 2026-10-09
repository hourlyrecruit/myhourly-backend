package com.my_hourly.leave.repository;

import com.my_hourly.leave.entity.LeaveRequestMonthAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestMonthAllocationRepository
        extends JpaRepository<LeaveRequestMonthAllocation, Long> {
}
