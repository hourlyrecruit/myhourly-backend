package com.my_hourly.leave.api.response;

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

    private Integer monthlyGuideline;

    private boolean carryForwardAllowed;

    private Boolean active;
}