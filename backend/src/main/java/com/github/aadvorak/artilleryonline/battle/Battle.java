package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class Battle {

    public static final long TIME_STEP_MS = 20;

    private BattleModel model;

    private final String id = UUID.randomUUID().toString();

    private final long beginTime = System.currentTimeMillis();

    private long time;

    private BattleStage battleStage;

    private Map<String, Queue<UserCommand>> userCommandQueues;

    public synchronized void setStageAndResetTime(BattleStage battleStage) {
        this.battleStage = battleStage;
        this.time = 0;
    }
}
