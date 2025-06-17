package com.github.aadvorak.artilleryonline.config;

import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    private final ApplicationLimits applicationLimits;

    private final int corePoolSize;

    private final int maxBattlePoolSize;

    public AsyncConfig(ApplicationLimits applicationLimits) {
        this.applicationLimits = applicationLimits;
        corePoolSize = Runtime.getRuntime().availableProcessors();
        maxBattlePoolSize = Math.max(corePoolSize, applicationLimits.getMaxBattles());
        log.info("Async config corePoolSize = {}, maxBattlePoolSize = {}", corePoolSize, maxBattlePoolSize);
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
        executor.setMaxPoolSize(maxBattlePoolSize * 2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("SendBattleUpdatesAsync-");
        executor.initialize();
        return executor;
    }
}
