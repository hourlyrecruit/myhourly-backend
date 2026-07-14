package com.my_hourly.organization.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDesignationRequest {

    @NotBlank
    private String designationName;

    @NotNull
    private Long departmentId;

    private String description;

    private boolean active;

}
