package com.my_hourly.leave.api.response;

import com.my_hourly.leave.enums.MonthType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long leaveTypeId;

    private String leaveType;

    private Integer year;

    private MonthType month;

    private Integer allocatedLeaves;

    private Integer carriedForwardLeaves;

    private Integer usedLeaves;

    private Integer expiredLeaves;

    private Integer remainingLeaves;

}