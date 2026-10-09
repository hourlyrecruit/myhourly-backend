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

    /**
     * Days approved as PAID and deducted from the annual balance. Null while
     * the request is pending or rejected.
     */
    private Integer paidDays;

    /**
     * Days approved as LOP (Loss of Pay) and NOT deducted from the annual
     * balance. Null while the request is pending or rejected.
     */
    private Integer lopDays;

    /**
     * The authenticated approver, populated only once the request is approved.
     */
    private Long approvedById;

    private String approvedByName;

    private String approvedByCode;

//    private String rejectionReason;

}
