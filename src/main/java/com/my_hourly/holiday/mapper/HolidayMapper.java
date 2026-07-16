package com.my_hourly.holiday.mapper;

import com.my_hourly.holiday.api.request.CreateHolidayRequest;
import com.my_hourly.holiday.api.request.UpdateHolidayRequest;
import com.my_hourly.holiday.api.response.HolidayCalendarResponse;
import com.my_hourly.holiday.api.response.HolidayResponse;
import com.my_hourly.holiday.entity.Holiday;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HolidayMapper {

    public Holiday toEntity(CreateHolidayRequest request) {

        return Holiday.builder()
                .holidayDate(request.getHolidayDate())
                .holidayName(request.getHolidayName())
                .holidayType(request.getHolidayType())
                .description(request.getDescription())
                .attendanceAllowed(request.getAttendanceAllowed())
                .recurring(request.getRecurring())
                .active(true)
                .build();
    }

    public void updateEntity(
            Holiday holiday,
            UpdateHolidayRequest request
    ) {

        holiday.setHolidayDate(request.getHolidayDate());
        holiday.setHolidayName(request.getHolidayName());
        holiday.setHolidayType(request.getHolidayType());
        holiday.setDescription(request.getDescription());
        holiday.setAttendanceAllowed(request.getAttendanceAllowed());
        holiday.setRecurring(request.getRecurring());
        holiday.setActive(request.getActive());

    }

    public HolidayResponse toResponse(Holiday holiday) {

        return HolidayResponse.builder()
                .id(holiday.getId())
                .holidayDate(holiday.getHolidayDate())
                .holidayName(holiday.getHolidayName())
                .holidayType(holiday.getHolidayType())
                .description(holiday.getDescription())
                .attendanceAllowed(holiday.getAttendanceAllowed())
                .recurring(holiday.getRecurring())
                .active(holiday.getActive())
                .build();

    }

    public HolidayCalendarResponse toCalendarResponse(
            Holiday holiday
    ) {

        return HolidayCalendarResponse.builder()
                .holidayDate(holiday.getHolidayDate())
                .holidayName(holiday.getHolidayName())
                .holidayType(holiday.getHolidayType())
                .build();

    }

    public List<HolidayResponse> toResponseList(
            List<Holiday> holidays
    ) {

        return holidays.stream()
                .map(this::toResponse)
                .toList();

    }

}
