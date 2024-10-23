package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
@Accessors(chain = true)
public class Battle {

    public static final long TIME_STEP_MS = 15;

    private BattleModel model;

    private final String id = UUID.randomUUID().toString();

    private final long beginTime = System.currentTimeMillis();

    private long absoluteTime = beginTime;

    private long time;

    private boolean paused = false;

    private boolean doStep = false;

    private boolean forceSend = false;

    private BattleStage battleStage;

    private Map<Long, Queue<UserCommand>> userCommandQueues;

    private Map<Long, String> userNicknameMap;

    private Queue<DebugCommand> debugCommands = new ConcurrentLinkedQueue<>();

    private String tracking;

    public void setStageAndResetTime(BattleStage battleStage) {
        this.battleStage = battleStage;
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
