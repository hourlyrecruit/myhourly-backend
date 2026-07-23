package com.my_hourly.holiday.api.response;

import com.my_hourly.holiday.entity.HolidayType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayResponse {

    private Long id;

    private LocalDate holidayDate;

    private String holidayName;

    private HolidayType holidayType;

    private String description;

    private Boolean attendanceAllowed;

    private Boolean recurring;

    private Boolean active;

}