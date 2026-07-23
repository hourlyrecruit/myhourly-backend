package com.my_hourly.leave.api.request;

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

    /**
     * Recommended leave days per month.
     * Used only by the month-end scheduler when carryForwardAllowed = false.
     * Defaults to 2 if not provided.
     */
    @Min(value = 0, message = "Monthly guideline cannot be negative.")
    private Integer monthlyGuideline;

    /**
     * When true, unused monthly guideline does NOT expire at month-end.
     */
    private boolean carryForwardAllowed;
}
