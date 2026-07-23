package com.my_hourly.attendance.api.request;

import com.my_hourly.attendance.entity.BreakType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakStartRequest {

    @NotNull(message = "Break type is required.")
    private BreakType breakType;

}