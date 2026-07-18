package com.my_hourly.settings.attendance.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.settings.attendance.dto.request.AttendanceSettingsRequest;
import com.my_hourly.settings.attendance.dto.response.AttendanceSettingsResponse;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import com.my_hourly.settings.attendance.mapper.AttendanceSettingsMapper;
import com.my_hourly.settings.attendance.repository.AttendanceSettingsRepository;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceSettingsServiceImpl implements AttendanceSettingsService {

    private final AttendanceSettingsRepository attendanceSettingsRepository;
    private final AttendanceSettingsMapper attendanceSettingsMapper;

    @Override
    @Transactional(readOnly = true)
    public AttendanceSettings getSettings() {
        return attendanceSettingsRepository.findFirstByActiveTrue()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance settings not found.", ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSettingsResponse getAttendanceSettings() {
        AttendanceSettings attendanceSettings = getSettings();
        return attendanceSettingsMapper.toResponse(attendanceSettings);
    }

    @Override
    public AttendanceSettingsResponse updateAttendanceSettings(
            AttendanceSettingsRequest request) {

        AttendanceSettings attendanceSettings = getSettings();

        attendanceSettingsMapper.updateEntity(request, attendanceSettings);

        AttendanceSettings updatedSettings =
                attendanceSettingsRepository.save(attendanceSettings);

        return attendanceSettingsMapper.toResponse(updatedSettings);
    }
}