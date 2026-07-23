package com.my_hourly.master.api.response;

import lombok.*;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignationResponse {

    private Long id;

    private String designationCode;

    private String designationName;

    private Long departmentId;

    private String departmentName;

    private String description;

    private boolean active;

}
