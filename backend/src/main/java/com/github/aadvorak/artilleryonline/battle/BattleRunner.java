package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import com.github.aadvorak.artilleryonline.collection.BattleStateUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.dto.response.BattleModelStateResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class BattleRunner implements Runnable {

    private final Battle battle;

    private final UserBattleMap userBattleMap;

    private final BattleUpdatesQueue battleUpdatesQueue;

    private final BattleStateUpdatesQueue battleStateUpdatesQueue;

    private final ApplicationSettings applicationSettings;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor = new WaitingBattleStepProcessor();
    private final ActiveBattleStepProcessor activeBattleStepProcessor = new ActiveBattleStepProcessor();

    @Override
    public void run() {
        while (!BattleStage.FINISHED.equals(battle.getBattleStage())
                && !battle.getUserNicknameMap().isEmpty()) {
            try {
                Thread.sleep(Battle.TIME_STEP_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processBattleStep();
            setBattleUpdatedByTimeout();
            sendBattleToUpdatesQueue();
            resetUpdatedFlags();
        }
        removeBattleFromMap();
        log.info("Battle finished: {}, map size {}", battle.getId(), userBattleMap.size());
    }

    private void processBattleStep() {
        if (BattleStage.WAITING.equals(battle.getBattleStage())) {
            waitingBattleStepProcessor.processStep(battle);
        } else if (BattleStage.ACTIVE.equals(battle.getBattleStage())) {
            activeBattleStepProcessor.processStep(battle);
        }
    }

    private void removeBattleFromMap() {
        battle.getUserNicknameMap().keySet().forEach(userBattleMap::remove);
    }

    private void setBattleUpdatedByTimeout() {
        var battleModel = battle.getModel();
        if (!battleModel.isUpdated()
                && battle.getAbsoluteTime() - battleModel.getLastUpdateTime()
                > applicationSettings.getBattleUpdateTimeout()) {
            battleModel.setUpdated(true);
        }
    }

    private void sendBattleToUpdatesQueue() {
        if (battle.getModel().isUpdated()
                || battle.isForceSend()
                || (!applicationSettings.isClientProcessing() && !battle.isPaused())) {
            battleUpdatesQueue.add(battle);
        } else {
            sendBattleStateToUpdatesQueue();
        }
    }

    private void sendBattleStateToUpdatesQueue() {
        var vehicles = battle.getModel().getVehicles().entrySet();
        if (vehicles.stream().anyMatch(vehicleEntry -> vehicleEntry.getValue().isUpdated())) {
            var vehicleStates = vehicles.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
            var shellStates = battle.getModel().getShells().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
            battleStateUpdatesQueue.add(new BattleStateResponse()
                    .setId(battle.getId())
                    .setTime(battle.getTime())
                    .setState(new BattleModelStateResponse()
                            .setVehicles(vehicleStates)
                            .setShells(shellStates)));
        }
    }

    private void resetUpdatedFlags() {
        battle.getModel().setUpdated(false);
        battle.getModel().getVehicles().values().forEach(vehicle -> vehicle.setUpdated(false));
    }
}
