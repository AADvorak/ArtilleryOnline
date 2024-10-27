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
    public UserRoomMap userRoomMap() {
        return new UserRoomMap();
    }

    @Bean
    public RoomInvitationMap roomInvitationMap() {
        return new RoomInvitationMap();
    }

    @Bean
    public BattleUpdatesQueue battleUpdatesQueue() {
        return new BattleUpdatesQueue();
    }

    @Bean
    public BattleStateUpdatesQueue battleStateUpdatesQueue() {
        return new BattleStateUpdatesQueue();
    }
}
