package com.snazzyrobot.peeper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "comparisonTaskExecutor")
    public Executor comparisonTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setAwaitTerminationSeconds(120);
        executor.setThreadNamePrefix("comparison-");
        executor.initialize();

        // Rejection policy: what to do when queue is full
        executor.setRejectedExecutionHandler((r, e) -> {
            throw new RuntimeException("Comparison task rejected - queue capacity full");
        });

        // Proper shutdown behavior
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}