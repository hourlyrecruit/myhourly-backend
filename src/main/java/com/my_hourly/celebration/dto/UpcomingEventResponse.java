package com.my_hourly.celebration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingEventResponse {
    private Long id;
    private  String title;
    private String location;
    private String description;
    private LocalDate eventDate;
}
