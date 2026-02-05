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

    @Bean
    public RoomMap roomMap() {
        return new RoomMap();
    }

    @Bean
    public UserRoomMap userRoomMap() {
        return new UserRoomMap();
    }

    @Bean
    public RoomInvitationMap roomInvitationMap() {
        return new RoomInvitationMap();
    }

    @Bean
    public UserMessageMap userMessageMap() {
        return new UserMessageMap();
    }

    @Bean
    public BattleTrackingMap battleTrackingMap() {
        return new BattleTrackingMap();
    }
}
