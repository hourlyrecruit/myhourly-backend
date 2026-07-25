package com.my_hourly.celebration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentResponse {

    private Long id;
    private Long userId;
    private String username;
    private String comment;
    private byte[] profilePhoto;
    private LocalDateTime commentedAt;
}
