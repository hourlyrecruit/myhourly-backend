package com.my_hourly.leave.api.request;

import com.my_hourly.leave.enums.LeaveAllocationType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required.")
    @Size(max = 50, message = "Leave type name cannot exceed 50 characters.")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    @NotNull(message = "Paid status is required.")
    private Boolean paid;

    @NotNull(message = "Allocated days are required.")
    @Min(value = 0, message = "Allocated days cannot be negative.")
    private Integer allocatedDays;

    @NotNull(message = "Allocation type is required.")
    private LeaveAllocationType allocationType;

    private boolean carryForwardAllowed;

    private Integer maxCarryForwardDays;

    private boolean expireUnusedLeaves;
}
