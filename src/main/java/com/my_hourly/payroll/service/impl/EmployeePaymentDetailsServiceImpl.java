package com.my_hourly.payroll.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.DuplicateResourceException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.payroll.dto.request.CreateEmployeePaymentDetailsRequest;
import com.my_hourly.payroll.dto.request.UpdateEmployeePaymentDetailsRequest;
import com.my_hourly.payroll.dto.response.EmployeePaymentDetailsResponse;
import com.my_hourly.payroll.entity.EmployeePaymentDetails;
import com.my_hourly.payroll.repository.EmployeePaymentDetailsRepository;
import com.my_hourly.payroll.service.EmployeePaymentDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeePaymentDetailsServiceImpl
        implements EmployeePaymentDetailsService {

    private final EmployeeRepository employeeRepository;
    private final EmployeePaymentDetailsRepository paymentDetailsRepository;

    @Override
    public EmployeePaymentDetailsResponse create(
            CreateEmployeePaymentDetailsRequest request) {

        Employee employee = getEmployee(request.getEmployeeId());

        validateEmployee(employee);

        validateDuplicate(employee.getId());

        EmployeePaymentDetails paymentDetails = EmployeePaymentDetails.builder()
                .employee(employee)
                .panNumber(request.getPanNumber())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .paymentMode(request.getPaymentMode())
                .uanNumber(request.getUanNumber())
                .pfNumber(request.getPfNumber())
                .esiNumber(request.getEsiNumber())
                .build();

        paymentDetails = paymentDetailsRepository.save(paymentDetails);

        return mapToResponse(paymentDetails);
    }

    @Override
    public EmployeePaymentDetailsResponse update(
            Long employeeId,
            UpdateEmployeePaymentDetailsRequest request) {

        EmployeePaymentDetails paymentDetails =
                getPaymentDetails(employeeId);

        paymentDetails.setPanNumber(request.getPanNumber());
        paymentDetails.setBankName(request.getBankName());
        paymentDetails.setAccountNumber(request.getAccountNumber());
        paymentDetails.setIfscCode(request.getIfscCode());
        paymentDetails.setPaymentMode(request.getPaymentMode());
        paymentDetails.setUanNumber(request.getUanNumber());
        paymentDetails.setPfNumber(request.getPfNumber());
        paymentDetails.setEsiNumber(request.getEsiNumber());

        paymentDetails = paymentDetailsRepository.save(paymentDetails);

        return mapToResponse(paymentDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeePaymentDetailsResponse getByEmployeeId(
            Long employeeId) {

        return mapToResponse(getPaymentDetails(employeeId));
    }

    @Override
    public void delete(Long employeeId) {

        EmployeePaymentDetails paymentDetails =
                getPaymentDetails(employeeId);

        paymentDetailsRepository.delete(paymentDetails);
    }

    private Employee getEmployee(Long employeeId) {

        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id : " + employeeId, ErrorCode.EMPLOYEE_NOT_FOUND));
    }

    private EmployeePaymentDetails getPaymentDetails(
            Long employeeId) {

        return paymentDetailsRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee payment details not found.", ErrorCode.RESOURCE_NOT_FOUND));
    }

    private void validateDuplicate(Long employeeId) {

        if (paymentDetailsRepository.existsByEmployeeId(employeeId)) {

            throw new DuplicateResourceException(
                    "Payment details already exist for this employee.", ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        }
    }

    /**
     * Validate employee before payroll setup.
     */
    private void validateEmployee(Employee employee) {

        if (!employee.isActive()) {

            throw new BadRequestException(
                    "Payment details can only be created for active employees.", ErrorCode.BAD_REQUEST);
        }
    }

    private EmployeePaymentDetailsResponse mapToResponse(
            EmployeePaymentDetails paymentDetails) {

        Employee employee = paymentDetails.getEmployee();

        return EmployeePaymentDetailsResponse.builder()
                .id(paymentDetails.getId())
                .employeeId(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .panNumber(paymentDetails.getPanNumber())
                .bankName(paymentDetails.getBankName())
                .accountNumber(paymentDetails.getAccountNumber())
                .ifscCode(paymentDetails.getIfscCode())
                .paymentMode(paymentDetails.getPaymentMode())
                .uanNumber(paymentDetails.getUanNumber())
                .pfNumber(paymentDetails.getPfNumber())
                .esiNumber(paymentDetails.getEsiNumber())
                .createdAt(paymentDetails.getCreatedAt())
                .updatedAt(paymentDetails.getUpdatedAt())
                .build();
    }

}
