package com.my_hourly.leave.api.response;

import com.my_hourly.leave.enums.LeaveStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponse {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long leaveTypeId;

    private String leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private String reason;

    private LeaveStatus status;

//    private String rejectionReason;

}
