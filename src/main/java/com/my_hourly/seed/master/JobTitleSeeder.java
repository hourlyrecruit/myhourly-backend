package com.my_hourly.seed.master;

import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobTitleSeeder {

    private final JobTitleRepository jobTitleRepository;
    private final DesignationRepository designationRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
//        if (jobTitleRepository.count() > 1) {
//            log.info("Job Titles already seeded. Skipping...");
//            return;
//        }

        List<Map<String, String>> records = csvReader.readCsv("seed/job_titles.csv");
        for (Map<String, String> record : records) {
            String title = record.get("job_title");
            if (!jobTitleRepository.existsByJobTitle(title)) {
                String desigCode = record.get("designation_code");
                Optional<Designation> desigOpt = designationRepository.findByDesignationCode(desigCode);

                if (desigOpt.isPresent()) {
                    JobTitle jobTitle = JobTitle.builder()
                            .jobTitleCode(generateJobTitleCode())
                            .jobTitle(title)
                            .designation(desigOpt.get())
                            .active(true)
                            .build();
                    jobTitleRepository.save(jobTitle);
                    log.info("Seeded job title: {}", title);
                } else {
                    log.warn("Designation not found for code: {}", desigCode);
                }
            }
        }
    }

    private String generateJobTitleCode() {
        JobTitle lastJobTitle = jobTitleRepository.findTopByOrderByJobTitleCodeDesc();
        if (lastJobTitle == null) {
            return "JT0001";
        }
        int number = Integer.parseInt(lastJobTitle.getJobTitleCode().substring(2));
        return String.format("JT%04d", number + 1);
    }
}

