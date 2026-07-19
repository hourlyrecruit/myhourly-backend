package com.my_hourly.leave.api.response;

import com.my_hourly.leave.enums.LeaveAllocationType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean paid;

    private Integer allocatedDays;

    private LeaveAllocationType allocationType;

    private Boolean active;
}