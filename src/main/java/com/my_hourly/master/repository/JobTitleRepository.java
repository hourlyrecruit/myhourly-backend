package com.my_hourly.master.repository;

import com.my_hourly.master.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobTitleRepository extends JpaRepository<JobTitle, Long> {

    boolean existsByJobTitleAndDesignationId(
            String jobTitle,
            Long designationId
    );

    Page<JobTitle> findByJobTitleContainingIgnoreCase(
            String jobTitle,
            Pageable pageable
    );

    JobTitle findTopByOrderByJobTitleCodeDesc();

}