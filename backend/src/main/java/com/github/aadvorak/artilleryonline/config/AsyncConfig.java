package com.github.aadvorak.artilleryonline.config;

import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private final ApplicationLimits applicationLimits;

    private final int corePoolSize;

    private final int maxPoolSize;

    public AsyncConfig(ApplicationLimits applicationLimits) {
        this.applicationLimits = applicationLimits;
        corePoolSize = Runtime.getRuntime().availableProcessors();
        maxPoolSize = Math.max(corePoolSize, applicationLimits.getMaxBattles());
    }

    @Bean(name = "runBattleExecutor")
    public Executor runBattleExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(applicationLimits.getMaxBattles());
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("RunBattleAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "sendBattleUpdatesExecutor")
    public Executor sendBattleUpdatesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize * 2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("SendBattleUpdatesAsync-");
        executor.initialize();
        return executor;
    }
}
