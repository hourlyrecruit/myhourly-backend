package com.my_hourly.organization.api.response;

import lombok.*;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;

    private String departmentCode;

    private String departmentName;

    private Long branchId;

    private String branchName;

    private String description;

    private boolean active;

}
