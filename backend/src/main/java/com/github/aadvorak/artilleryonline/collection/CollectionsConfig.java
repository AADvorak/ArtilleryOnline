package com.github.aadvorak.artilleryonline.collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionsConfig {

    @Bean
    public UserBattleQueue userBattleQueue() {
        return new UserBattleQueue();
    }

    @Bean
    public UserBattleMap userBattleMap() {
        return new UserBattleMap();
    }
}
