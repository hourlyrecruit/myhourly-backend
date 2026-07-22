package com.my_hourly.leave.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.leave.api.request.LeaveTypeRequest;
import com.my_hourly.leave.api.response.LeaveTypeResponse;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.mapper.LeaveTypeMapper;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeEntity(Long leaveTypeId) {

        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave Type id: {leaveTypeId}",
                       ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {

        validateLeaveTypeName(request.getName(), null);

        LeaveType leaveType = leaveTypeMapper.toEntity(request);
        leaveType.setActive(true);

        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);

        return leaveTypeMapper.toResponse(savedLeaveType);
    }

    @Override
    public LeaveTypeResponse updateLeaveType(
            Long leaveTypeId,
            LeaveTypeRequest request) {

        LeaveType leaveType = getLeaveTypeEntity(leaveTypeId);

       validateLeaveTypeName(request.getName(), leaveTypeId);

        leaveTypeMapper.updateEntity(request, leaveType);

        LeaveType updatedLeaveType = leaveTypeRepository.save(leaveType);

        return leaveTypeMapper.toResponse(updatedLeaveType);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveTypeResponse getLeaveType(Long leaveTypeId) {

        return leaveTypeMapper.toResponse(
                getLeaveTypeEntity(leaveTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getAllLeaveTypes() {

        return leaveTypeRepository.findAll()
                .stream()
                .map(leaveTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getActiveLeaveTypes() {

        return leaveTypeRepository.findByActiveTrue()
                .stream()
                .map(leaveTypeMapper::toResponse)
                .toList();
    }

    @Override
    public LeaveTypeResponse activateLeaveType(Long leaveTypeId) {

        LeaveType leaveType = getLeaveTypeEntity(leaveTypeId);

        if (Boolean.TRUE.equals(leaveType.getActive())) {
            throw new BadRequestException(
                    "Leave type is already active.", ErrorCode.LEAVE_ALREADY_EXIST);
        }

        leaveType.setActive(true);

        LeaveType updatedLeaveType = leaveTypeRepository.save(leaveType);

        return leaveTypeMapper.toResponse(updatedLeaveType);
    }

    @Override
    public LeaveTypeResponse deactivateLeaveType(Long leaveTypeId) {

        LeaveType leaveType = getLeaveTypeEntity(leaveTypeId);

        if (Boolean.FALSE.equals(leaveType.getActive())) {
            throw new BadRequestException(
                    "Leave type is already inactive.", ErrorCode.LEAVE_ALREADY_EXIST);
        }

        leaveType.setActive(false);

        LeaveType updatedLeaveType = leaveTypeRepository.save(leaveType);

        return leaveTypeMapper.toResponse(updatedLeaveType);
    }

    /**
     * Validate duplicate leave type name.
     */
    private void validateLeaveTypeName(
            String name,
            Long leaveTypeId) {

        boolean exists;

        if (leaveTypeId == null) {

            exists = leaveTypeRepository
                    .existsByNameIgnoreCase(name);

        } else {

            exists = leaveTypeRepository
                    .existsByNameIgnoreCaseAndIdNot(
                            name,
                            leaveTypeId);

        }

        if (exists) {
            throw new BadRequestException(
                    "Leave type with name '" + name + "' already exists.Most probably wrong ID given", ErrorCode.LEAVE_ALREADY_EXIST);
        }
    }
}