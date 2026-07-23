package com.my_hourly.celebration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaggedEmployeeResponse {

    private Long employeeId;
    private String employeeName;
    private byte[] profilePhoto;
}