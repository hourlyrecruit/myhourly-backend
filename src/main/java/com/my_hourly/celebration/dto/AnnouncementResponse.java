package com.my_hourly.celebration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean active;

}