package com.my_hourly.attendance.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRegularizationRequest {

    @NotNull(message = "From date is required.")
    private LocalDate fromDate;

    @NotNull(message = "To date is required.")
    private LocalDate toDate;

    @NotBlank(message = "Reason is required.")
    private String reason;

    @NotEmpty(message = "At least one detail is required.")
    @Valid
    private List<CreateRegularizationDetailRequest> details;
}
