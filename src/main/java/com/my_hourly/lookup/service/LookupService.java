package com.my_hourly.lookup.service;

import com.my_hourly.lookup.api.response.EmployeeIdName;
import com.my_hourly.lookup.api.response.LookupResponse;
import com.my_hourly.lookup.api.response.ReportingManagerLookupResponse;

import java.util.List;

public interface LookupService {

    List<LookupResponse> getDepartments();

    List<LookupResponse> getDesignations(Long departmentId);

    List<LookupResponse> getJobTitles(Long designationId);

    List<ReportingManagerLookupResponse> getReportingManagers();

    List<EmployeeIdName> employeesNameAndId();

}
