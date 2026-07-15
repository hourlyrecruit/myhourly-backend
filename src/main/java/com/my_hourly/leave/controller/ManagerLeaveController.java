package com.my_hourly.leave.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.service.LeaveService;

@RestController
@RequestMapping("/api/manager/leaves")
public class ManagerLeaveController {

    @Autowired
    private LeaveService leaveService;

    

    @GetMapping("/pending")
    public List<LeaveResponse> getPendingLeaves() {

        return leaveService.getPendingLeaves();

    }


    @PutMapping("/approve/{leaveId}")
    public LeaveResponse approveLeave(
            @PathVariable Long leaveId) {

        return leaveService.approveLeave(leaveId);

    }

    @PutMapping("/reject/{leaveId}")
    public LeaveResponse rejectLeave(
            @PathVariable Long leaveId) {

        return leaveService.rejectLeave(leaveId);

    }

}