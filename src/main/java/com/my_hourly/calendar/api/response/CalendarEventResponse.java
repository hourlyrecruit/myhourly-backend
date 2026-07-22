package com.my_hourly.calendar.api.response;

import com.my_hourly.calendar.enums.CalendarEventType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventResponse {

    private LocalDate eventDate;

    private String title;

    private String description;

    private CalendarEventType eventType;

    private String color;

    private Long referenceId;
}