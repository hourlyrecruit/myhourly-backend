package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16SalaryRequest;
import com.my_hourly.form_16.dto.Form16SalaryResponse;

public interface Form16SalaryService {

    Form16SalaryResponse createSalary(
            Long form16Id,
            Form16SalaryRequest request
    );

    Form16SalaryResponse getSalaryByForm16Id(
            Long form16Id
    );

    Form16SalaryResponse updateSalary(
            Long form16Id,
            Form16SalaryRequest request
    );

    void deleteSalary(Long form16Id);
}
