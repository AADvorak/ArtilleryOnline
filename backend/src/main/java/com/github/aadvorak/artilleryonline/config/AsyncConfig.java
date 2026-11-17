package com.github.aadvorak.artilleryonline.config;

import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    private final int corePoolSize;

    private final int maxBattlePoolSize;

    public AsyncConfig(ApplicationLimits applicationLimits) {
        corePoolSize = Math.min(applicationLimits.getMaxBattles(), Runtime.getRuntime().availableProcessors());
        maxBattlePoolSize = Math.max(corePoolSize, applicationLimits.getMaxBattles());
        log.info("Async config corePoolSize = {}, maxBattlePoolSize = {}", corePoolSize, maxBattlePoolSize);
    }

    @Bean(name = "runBattleExecutor")
    public ThreadPoolTaskExecutor runBattleExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxBattlePoolSize);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("RunBattleAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "sendBattleUpdatesExecutor")
    public ThreadPoolTaskExecutor sendBattleUpdatesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxBattlePoolSize * 2);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("SendBattleUpdatesAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "battleQueueConsumerExecutor")
    public ThreadPoolTaskExecutor battleQueueConsumerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("BattleQueueConsumer-");
        executor.setRejectedExecutionHandler(
                new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(5);
        executor.initialize();
        return executor;
    }
}
