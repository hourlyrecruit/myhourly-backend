package com.my_hourly.leave.api.request;

import com.my_hourly.leave.enums.LeaveAction;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveActionRequest {

    @NotNull(message = "Action is required.")
    private LeaveAction action;

    @Size(max = 500)
    private String reason;

    @AssertTrue(message = "Reason is required when rejecting a leave request.")
    public boolean isReasonValid() {
        return action != LeaveAction.REJECT ||
                (reason != null && !reason.isBlank());
    }

}