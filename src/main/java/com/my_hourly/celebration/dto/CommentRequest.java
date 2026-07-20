package com.my_hourly.celebration.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentRequest {
    private Long employeeId;
    private String comment;


}