package com.my_hourly.calendar.service;

import com.my_hourly.calendar.api.response.CalendarResponse;
import com.my_hourly.calendar.enums.CalendarEventType;
import com.my_hourly.calendar.enums.CalendarView;

import java.util.List;

public interface CalendarService {

    CalendarResponse getCalendar(
            Integer month,
            Integer year,
            CalendarView view,
            List<CalendarEventType> eventTypes
    );
//
//    CalendarResponse getCalendar(
//            Integer month,
//            Integer year,
//            CalendarView view,
//            CalendarEventType eventTypes
//    );

}