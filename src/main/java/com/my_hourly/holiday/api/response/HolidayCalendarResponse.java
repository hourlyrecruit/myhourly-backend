package com.my_hourly.holiday.api.response;

import com.my_hourly.holiday.entity.HolidayType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayCalendarResponse {

    private LocalDate holidayDate;

    private String holidayName;

    private HolidayType holidayType;

}
