package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class Battle {

    public static final long TIME_STEP_MS = 10;

    public static final long FINISH_TIMEOUT = 2000;

    private BattleModel model;

    private final String id = UUID.randomUUID().toString();

    private final long beginTime = System.currentTimeMillis();

    private long absoluteTime = beginTime;

    private long time;

    private long duration;

    private long droneLaunchTime;

    private long boxDropTime;

    private final FpsCalculator fpsCalculator = new FpsCalculator(20);

    private BattleStage battleStage;

    private BattleType type;

    private boolean stageUpdated = false;

    private Map<Long, User> userMap;

    private Map<String, String> playerVehicleNameMap;

    private Set<Long> activeUserIds;

    private BattleBotsData botsData = new BattleBotsData();

    private BattleDebug debug = new BattleDebug();

    private Room room;

    private BattleQueues queues = new BattleQueues();

    private BattleAggregated aggregated = new BattleAggregated();

    private Map<String, Integer> nicknameTeamMap = new HashMap<>();

    private Integer winnerTeamId;

    private Long finishTime;

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
            currentTimeStep = TIME_STEP_MS + BattleUtils.generateRandom(1, 5);
        }
        time += currentTimeStep;
        absoluteTime = currentTime;
        model.setCurrentTimeStepSecs((double) currentTimeStep / 1000);
        fpsCalculator.addTimeStep((int) currentTimeStep);
    }

    public long getMaxTime() {
        if (BattleStage.ACTIVE.equals(battleStage)) {
            return duration;
        }
        return battleStage.getMaxTime();
    }

    public Set<String> getTeamNicknames(int teamId) {
        return nicknameTeamMap.entrySet().stream()
                .filter(entry -> entry.getValue() == teamId)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

    }

    public Boolean getWon(String nickname) {
        if (winnerTeamId != null) {
            return nicknameTeamMap.get(nickname).equals(winnerTeamId);
        }
        return null;
    }
}
