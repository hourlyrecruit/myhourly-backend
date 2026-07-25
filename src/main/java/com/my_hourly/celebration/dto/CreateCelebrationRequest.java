package com.my_hourly.celebration.dto;
import java.util.List;

import com.my_hourly.celebration.entity.CelebrationType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCelebrationRequest {

    private String title;
    private String description;
    private String imageUrl;
    private CelebrationType celebrationType;
    private List<Long> taggedEmployeeIds;

}