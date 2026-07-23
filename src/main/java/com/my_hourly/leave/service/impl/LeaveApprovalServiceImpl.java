package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveApprovalResponse;
import com.my_hourly.leave.entity.LeaveApproval;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.ApprovalLevel;
import com.my_hourly.leave.enums.LeaveAction;
import com.my_hourly.leave.mapper.LeaveApprovalMapper;
import com.my_hourly.leave.repository.LeaveApprovalRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeaveApprovalService;
import com.my_hourly.leave.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveApprovalServiceImpl
        implements LeaveApprovalService {

    private final LeaveApprovalRepository leaveApprovalRepository;
    private final LeaveApprovalMapper leaveApprovalMapper;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public void createApproval(
            LeaveRequest leaveRequest,
            Employee approvedBy,
            ApprovalLevel approvalLevel,
            LeaveAction action,
            String remarks) {

        LeaveApproval approval = LeaveApproval.builder()
                .leaveRequest(leaveRequest)
                .approvedBy(approvedBy)
                .approvalLevel(approvalLevel)
                .action(action)
                .remarks(remarks)
                .build();

        leaveApprovalRepository.save(approval);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveApprovalResponse> getApprovalHistory(
            Long leaveRequestId) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(leaveRequestId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Leave Request id: " + leaveRequestId,
                                ErrorCode.RESOURCE_NOT_FOUND));

        return leaveApprovalRepository
                .findByLeaveRequestOrderByCreatedAtAsc(
                        leaveRequest)
                .stream()
                .map(leaveApprovalMapper::toResponse)
                .toList();
    }

}
