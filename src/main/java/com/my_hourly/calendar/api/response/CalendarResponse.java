package com.my_hourly.calendar.api.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {

    private Integer year;

    private Integer month;

    private List<CalendarEventResponse> events;

}
