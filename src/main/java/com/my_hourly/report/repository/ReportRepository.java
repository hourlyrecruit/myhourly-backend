package com.my_hourly.report.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.report.entity.Report;
import com.my_hourly.report.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByGeneratedBy(Employee employee);
    List<Report> findByReportType(ReportType reportType);

}