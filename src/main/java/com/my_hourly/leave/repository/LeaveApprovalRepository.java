package com.my_hourly.leave.repository;

import com.my_hourly.leave.entity.LeaveApproval;
import com.my_hourly.leave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveApprovalRepository
        extends JpaRepository<LeaveApproval, Long> {

    List<LeaveApproval> findByLeaveRequestOrderByCreatedAtAsc(
            LeaveRequest leaveRequest);

    /**
     * Bulk-deletes every approval recorded for leave requests raised against the
     * given leave type. Must run before the requests themselves are deleted.
     */
    @Modifying
    @Query("""
            delete from LeaveApproval la
            where la.leaveRequest.id in (
                select lr.id from LeaveRequest lr
                where lr.leaveType.id = :leaveTypeId
            )
            """)
    int deleteByLeaveTypeId(@Param("leaveTypeId") Long leaveTypeId);

}