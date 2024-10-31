package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.dto.response.BattleModelStateResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.ws.BattleUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BattleRunner {

    private final UserBattleMap userBattleMap;

    private final ApplicationSettings applicationSettings;

    private final BattleUpdatesSender battleUpdatesSender;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor = new WaitingBattleStepProcessor();
    private final ActiveBattleStepProcessor activeBattleStepProcessor = new ActiveBattleStepProcessor();

    private final ModelMapper mapper = new ModelMapper();

    public void runBattle(Battle battle) {
        startUpdatesSender(battle);
        new Thread(() -> {
            while (!BattleStage.FINISHED.equals(battle.getBattleStage())
                    && !battle.getUserNicknameMap().isEmpty()) {
                try {
                    Thread.sleep(Battle.TIME_STEP_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                processBattleStep(battle);
                setBattleUpdatedByTimeout(battle);
                sendBattleToUpdatesQueue(battle);
                resetUpdatedFlags(battle);
            }
            stopUpdatesSender(battle);
            removeBattleFromMap(battle);
            removeBattleFromRoom(battle);
            log.info("Battle finished: {}, map size {}", battle.getId(), userBattleMap.size());
        }).start();
    }

    private void processBattleStep(Battle battle) {
        if (BattleStage.WAITING.equals(battle.getBattleStage())) {
            waitingBattleStepProcessor.processStep(battle);
        } else if (BattleStage.ACTIVE.equals(battle.getBattleStage())) {
            activeBattleStepProcessor.processStep(battle);
        }
    }

    private void removeBattleFromMap(Battle battle) {
        synchronized (userBattleMap) {
            battle.getUserNicknameMap().keySet().forEach(userBattleMap::remove);
        }
    }

    private void removeBattleFromRoom(Battle battle) {
        if (battle.getRoom() != null) {
            battle.getRoom().setBattle(null);
        }
    }

    private void startUpdatesSender(Battle battle) {
        battleUpdatesSender.start(battle.getQueues().getBattleUpdatesQueue());
    }

    private void stopUpdatesSender(Battle battle) {
        battle.getQueues().getBattleUpdatesQueue().add(new BattleResponse());
    }

    private void setBattleUpdatedByTimeout(Battle battle) {
        var battleModel = battle.getModel();
        if (!battleModel.isUpdated()
                && battle.getAbsoluteTime() - battleModel.getLastUpdateTime()
                > applicationSettings.getBattleUpdateTimeout()) {
            battleModel.setUpdated(true);
        }
    }

    private void sendBattleToUpdatesQueue(Battle battle) {
        if (battle.getModel().isUpdated()
                || battle.isForceSend()
                || (!applicationSettings.isClientProcessing() && !battle.isPaused())) {
            battle.getQueues().getBattleUpdatesQueue().add(mapper.map(battle, BattleResponse.class));
        } else {
            sendBattleStateToUpdatesQueue(battle);
        }
    }

    private void sendBattleStateToUpdatesQueue(Battle battle) {
        var vehicles = battle.getModel().getVehicles().entrySet();
        if (vehicles.stream().anyMatch(vehicleEntry -> vehicleEntry.getValue().isUpdated())) {
            var vehicleStates = vehicles.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
            var shellStates = battle.getModel().getShells().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
            battle.getQueues().getBattleUpdatesQueue().add(new BattleStateResponse()
                    .setId(battle.getId())
                    .setTime(battle.getTime())
                    .setState(new BattleModelStateResponse()
                            .setVehicles(vehicleStates)
                            .setShells(shellStates)));
        }
    }

    private void resetUpdatedFlags(Battle battle) {
        battle.getModel().setUpdated(false);
        battle.getModel().getVehicles().values().forEach(vehicle -> vehicle.setUpdated(false));
    }
}
