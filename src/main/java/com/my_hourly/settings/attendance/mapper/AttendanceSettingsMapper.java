package com.my_hourly.settings.attendance.mapper;

import com.my_hourly.settings.attendance.dto.request.AttendanceSettingsRequest;
import com.my_hourly.settings.attendance.dto.response.AttendanceSettingsResponse;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import org.springframework.stereotype.Component;

@Component
public class AttendanceSettingsMapper {

    public AttendanceSettingsResponse toResponse(AttendanceSettings entity) {

        if (entity == null) {
            return null;
        }

        return AttendanceSettingsResponse.builder()
                .id(entity.getId())
                .officeStartTime(entity.getOfficeStartTime())
                .officeEndTime(entity.getOfficeEndTime())
                .gracePeriodMinutes(entity.getGracePeriodMinutes())
                .minimumWorkingMinutes(entity.getMinimumWorkingMinutes())
                .halfDayWorkingMinutes(entity.getHalfDayWorkingMinutes())
                .checkoutCutoffMinutes(entity.getCheckoutCutoffMinutes())
                .overtimeEnabled(entity.getOvertimeEnabled())
                .attendanceRegularizationEnabled(entity.getAttendanceRegularizationEnabled())
                .multipleBreaksAllowed(entity.getMultipleBreaksAllowed())
                .maximumBreakMinutes(entity.getMaximumBreakMinutes())
                .maximumBreaksPerDay(entity.getMaximumBreaksPerDay())
                .weekendAttendanceAllowed(entity.getWeekendAttendanceAllowed())
                .holidayAttendanceAllowed(entity.getHolidayAttendanceAllowed())
                .lateMarkEnabled(entity.getLateMarkEnabled())
                .earlyExitEnabled(entity.getEarlyExitEnabled())
                .autoCheckoutEnabled(entity.getAutoCheckoutEnabled())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(
            AttendanceSettingsRequest request,
            AttendanceSettings entity) {

        entity.setOfficeStartTime(request.getOfficeStartTime());
        entity.setOfficeEndTime(request.getOfficeEndTime());
        entity.setGracePeriodMinutes(request.getGracePeriodMinutes());
        entity.setMinimumWorkingMinutes(request.getMinimumWorkingMinutes());
        entity.setHalfDayWorkingMinutes(request.getHalfDayWorkingMinutes());
        entity.setCheckoutCutoffMinutes(request.getCheckoutCutoffMinutes());
        entity.setOvertimeEnabled(request.getOvertimeEnabled());
        entity.setAttendanceRegularizationEnabled(request.getAttendanceRegularizationEnabled());
        entity.setMultipleBreaksAllowed(request.getMultipleBreaksAllowed());
        entity.setMaximumBreakMinutes(request.getMaximumBreakMinutes());
        entity.setMaximumBreaksPerDay(request.getMaximumBreaksPerDay());
    }
}