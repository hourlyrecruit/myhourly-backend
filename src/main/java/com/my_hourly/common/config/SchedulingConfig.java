package com.my_hourly.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configures a multi-threaded task scheduler for {@code @Scheduled} jobs.
 *
 * <p>Spring Boot's default scheduler is single-threaded, which means a long-running
 * job (e.g. daily birthday/holiday notification fan-out) blocks every other job
 * (e.g. the 1-minute missed-checkout check and the 5-minute notification processor).
 * Giving the scheduler a small pool lets independent jobs run in parallel.</p>
 */
@Slf4j
@Configuration
public class SchedulingConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("scheduled-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setErrorHandler(t -> log.error("Scheduled task failed", t));

        return scheduler;
    }
}
