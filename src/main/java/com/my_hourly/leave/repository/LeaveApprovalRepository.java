package com.my_hourly.leave.repository;

import com.my_hourly.leave.entity.LeaveApproval;
import com.my_hourly.leave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApprovalRepository
        extends JpaRepository<LeaveApproval, Long> {

    List<LeaveApproval> findByLeaveRequestOrderByCreatedAtAsc(
            LeaveRequest leaveRequest);

}