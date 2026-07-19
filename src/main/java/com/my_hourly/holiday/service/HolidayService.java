package com.my_hourly.holiday.service;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.holiday.api.request.CreateHolidayRequest;
import com.my_hourly.holiday.api.request.UpdateHolidayRequest;
import com.my_hourly.holiday.api.response.HolidayCalendarResponse;
import com.my_hourly.holiday.api.response.HolidayResponse;
import com.my_hourly.holiday.entity.HolidayType;


import java.time.LocalDate;
import java.util.List;

public interface HolidayService {

    HolidayResponse createHoliday(
            CreateHolidayRequest request
    );

    HolidayResponse updateHoliday(
            Long holidayId,
            UpdateHolidayRequest request
    );

    void deleteHoliday(
            Long holidayId
    );

    HolidayResponse getHolidayById(
            Long holidayId
    );

    PageResponse<HolidayResponse> getAllHolidays(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String holidayName,
            HolidayType holidayType,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    );

    List<HolidayCalendarResponse> getHolidayCalendar(
            Integer month,
            Integer year
    );

    List<HolidayResponse> getUpcomingHolidays();

}
