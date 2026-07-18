package com.my_hourly.holiday.api.request;

import com.my_hourly.holiday.entity.HolidayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHolidayRequest {

    @NotNull(message = "Holiday date is required.")
    private LocalDate holidayDate;

    @NotBlank(message = "Holiday name is required.")
    @Size(max = 100)
    private String holidayName;

    @NotNull(message = "Holiday type is required.")
    private HolidayType holidayType;

    @Size(max = 300)
    private String description;

    private Boolean attendanceAllowed;

    private Boolean recurring;

    private Boolean active;

}
