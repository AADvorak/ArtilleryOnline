package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.collection.BattleStateUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleQueues {

    private final BattleUpdatesQueue battleUpdatesQueue = new BattleUpdatesQueue();

    private final BattleStateUpdatesQueue battleStateUpdatesQueue = new BattleStateUpdatesQueue();
}
