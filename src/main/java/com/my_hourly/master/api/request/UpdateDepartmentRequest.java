package com.my_hourly.master.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentRequest {

    @NotBlank
    private String departmentName;

    private String description;

    private boolean active;

}