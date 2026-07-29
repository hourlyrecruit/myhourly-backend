package com.my_hourly.payroll.service.impl;

import com.my_hourly.payroll.dto.response.PayrollHistoryResponse;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.entity.PayrollHistory;
import com.my_hourly.payroll.enums.PayrollHistoryAction;
import com.my_hourly.payroll.repository.PayrollHistoryRepository;
import com.my_hourly.payroll.service.PayrollHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollHistoryServiceImpl implements PayrollHistoryService {

    private final PayrollHistoryRepository payrollHistoryRepository;

    @Override
    public void saveHistory(
            Payroll payroll,
            PayrollHistoryAction action,
            String remarks) {

        PayrollHistory history = PayrollHistory.builder()
                .payroll(payroll)
                .action(action)

                // TODO
                // SecurityUtils.getCurrentEmployee()

                .remarks(remarks)
                .build();

        payrollHistoryRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollHistoryResponse> getHistory(
            Long payrollId) {

        return payrollHistoryRepository
                .findByPayrollIdOrderByCreatedAtDesc(payrollId)
                .stream()
                .map(history ->
                        PayrollHistoryResponse.builder()
                                .id(history.getId())
                                .action(history.getAction())
                                .performedBy(
                                        history.getPerformedBy() != null
                                                ? history.getPerformedBy().getFirstName() + " " + history.getPerformedBy().getLastName()
                                                : null)
                                .remarks(history.getRemarks())
                                .createdAt(history.getCreatedAt())
                                .build())
                .toList();
    }
}
