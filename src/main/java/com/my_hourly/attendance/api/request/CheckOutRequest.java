package com.my_hourly.attendance.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequest {

    @NotNull(message = "Latitude is required.")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required.")
    private BigDecimal longitude;

}
