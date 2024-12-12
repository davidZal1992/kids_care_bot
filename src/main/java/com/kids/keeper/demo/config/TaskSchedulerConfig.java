package com.kids.keeper.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class TaskSchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("TaskScheduler-");
        taskScheduler.initialize();
        taskScheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
        return taskScheduler;
    }
}