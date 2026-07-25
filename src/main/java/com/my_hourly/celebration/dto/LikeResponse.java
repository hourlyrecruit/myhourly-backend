package com.my_hourly.celebration.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LikeResponse {
    private Long userId;
    private String username;
    private byte[] profilePhoto;
}
