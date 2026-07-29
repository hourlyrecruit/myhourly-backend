package com.my_hourly.payroll.service;

import com.my_hourly.payroll.dto.response.PayrollHistoryResponse;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.enums.PayrollHistoryAction;

import java.util.List;

public interface PayrollHistoryService {

    void saveHistory(
            Payroll payroll,
            PayrollHistoryAction action,
            String remarks);

    List<PayrollHistoryResponse> getHistory(
            Long payrollId);

}
