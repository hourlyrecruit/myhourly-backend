package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16SalaryRequest;
import com.my_hourly.form_16.entity.Form16Salary;

public interface Form16SalaryService {


    Form16Salary createSalary(
            Long form16Id,
            Form16SalaryRequest request
    );



    Form16Salary getSalaryByForm16Id(
            Long form16Id
    );



    Form16Salary updateSalary(
            Long form16Id,
            Form16SalaryRequest request
    );



    void deleteSalary(
            Long form16Id
    );
}