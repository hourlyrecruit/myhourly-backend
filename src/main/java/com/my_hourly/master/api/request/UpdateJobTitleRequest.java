package com.my_hourly.master.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobTitleRequest {

    @NotBlank(message = "Job title is required.")
    private String jobTitle;

    @NotNull(message = "Designation is required.")
    private Long designationId;

    private boolean active;

}
