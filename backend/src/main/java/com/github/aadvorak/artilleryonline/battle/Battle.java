package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class Battle {

    public static final long TIME_STEP_MS = 10;

    private BattleModel model;

    private final String id = UUID.randomUUID().toString();

    private final long beginTime = System.currentTimeMillis();

    private long absoluteTime = beginTime;

    private long time;

    private BattleStage battleStage;

    private BattleType type;

    private boolean stageUpdated = false;

    private Map<Long, User> userMap;

    private Map<Long, String> userVehicleNameMap;

    private Set<Long> activeUserIds;

    private BattleDebug debug = new BattleDebug();

    private Room room;

    private BattleQueues queues = new BattleQueues();

    public void setStageAndResetTime(BattleStage battleStage) {
        this.battleStage = battleStage;
        this.stageUpdated = true;
        this.absoluteTime = System.currentTimeMillis();
        this.time = 0;
    }

    public void increaseTime() {
        var previousTime = absoluteTime;
        var currentTime = System.currentTimeMillis();
        var currentTimeStep = currentTime - previousTime;
        if (currentTimeStep > TIME_STEP_MS * 10) {
            currentTimeStep = TIME_STEP_MS;
        }
        time += currentTimeStep;
        absoluteTime = currentTime;
        model.setCurrentTimeStepSecs((double) currentTimeStep / 1000);
    }
}
