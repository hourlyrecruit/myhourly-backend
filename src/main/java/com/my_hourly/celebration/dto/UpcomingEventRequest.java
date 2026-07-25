package com.my_hourly.celebration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class UpcomingEventRequest {
    @NotBlank
    private String title;
    private String location;
    private String description;
    private LocalDate eventDate;
}
