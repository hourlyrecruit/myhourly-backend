package com.my_hourly.celebration.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.my_hourly.celebration.entity.CelebrationType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CelebrationResponse {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private CelebrationType celebrationType;
    private LocalDateTime createdAt;
    private Long createdBy;
    private List<Long> taggedEmployees;
    private long totalLikes;
    private long totalComments;


}