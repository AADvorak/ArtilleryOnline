package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.events.BattleModelEvents;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.dto.response.BattleModelStateResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleUpdateResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.model.MessageSpecial;
import com.github.aadvorak.artilleryonline.model.UserBattleResult;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.service.BattleHistoryService;
import com.github.aadvorak.artilleryonline.service.MessageService;
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

    private final MessageService messageService;

    private final BattleHistoryService battleHistoryService;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor;

    private final ActiveBattleStepProcessor activeBattleStepProcessor;

    private final ModelMapper mapper = new ModelMapper();

    public void runBattle(Battle battle) {
        startUpdatesSender(battle);
        new Thread(() -> {
            try {
                while (!BattleStage.FINISHED.equals(battle.getBattleStage())
                        && !battle.getActiveUserIds().isEmpty()) {
                    try {
                        Thread.sleep(Battle.TIME_STEP_MS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    battle.getModel().setUpdates(new BattleModelUpdates());
                    battle.getModel().setEvents(new BattleModelEvents());
                    processBattleStep(battle);
                    setBattleUpdatedByTimeout(battle);
                    sendBattleToUpdatesQueue(battle);
                    resetUpdatedFlags(battle);
                }
                writeBattleToHistory(battle);
                createBattleFinishedMessages(battle);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                battle.setStageAndResetTime(BattleStage.ERROR);
                sendBattleToUpdatesQueue(battle);
            } finally {
                stopUpdatesSender(battle);
                removeBattleFromMap(battle);
                removeBattleFromRoom(battle);
                log.info("Battle finished: {}, map size {}", battle.getId(), userBattleMap.size());
            }
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
            battle.getActiveUserIds().forEach(userBattleMap::remove);
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

    private void createBattleFinishedMessages(Battle battle) {
        battle.getUserMap().values().forEach(user -> messageService.createMessage(user,
                "Battle has finished",
                new Locale().setCode(LocaleCode.BATTLE_FINISHED),
                new MessageSpecial().setUserBattleResult(createUserBattleResult(battle.getModel(), user))));
    }

    private UserBattleResult createUserBattleResult(BattleModel battleModel, User user) {
        return new ModelMapper().map(battleModel.getStatistics().get(user.getId()), UserBattleResult.class)
                .setSurvived(battleModel.getVehicles().get(user.getNickname()) != null);
    }

    private void writeBattleToHistory(Battle battle) {
        if (!battle.getType().equals(BattleType.TEST_DRIVE) && !battle.getType().equals(BattleType.DRONE_HUNT)) {
            battleHistoryService.writeHistory(battle);
        }
    }

    private void setBattleUpdatedByTimeout(Battle battle) {
        var battleModel = battle.getModel();
        if (!battleModel.isUpdated()
                && battle.getAbsoluteTime() - battleModel.getLastUpdateTime()
                > applicationSettings.getBattleUpdateTimeout()) {
            battleModel.setUpdated(true);
        }
        for (var missileModel: battleModel.getMissiles().values()) {
            if (missileModel.getUpdate().setUpdatedByTimeout(battle.getAbsoluteTime())) {
                setAllUpdatedByTimeout(battle);
                break;
            }
        }
        for (var droneModel: battleModel.getDrones().values()) {
            if (droneModel.getUpdate().setUpdatedByTimeout(battle.getAbsoluteTime())) {
                setAllUpdatedByTimeout(battle);
                break;
            }
        }
    }

    private void setAllUpdatedByTimeout(Battle battle) {
        var battleModel = battle.getModel();
        battleModel.getMissiles().values().forEach(model ->
                model.getUpdate().setUpdated(battle.getAbsoluteTime()));
        battleModel.getDrones().values().forEach(model ->
                model.getUpdate().setUpdated(battle.getAbsoluteTime()));
    }

    private void sendBattleToUpdatesQueue(Battle battle) {
        var updatesSent = sendBattleStateToUpdatesQueue(battle);
        if (battle.getModel().isUpdated()
                || (battle.getDebug().isForceSend()
                || (!applicationSettings.isClientProcessing() && !battle.getDebug().isPaused())
                && !updatesSent)) {
            var battleResponse = mapper.map(battle, BattleResponse.class);
            battleResponse.setPaused(battle.getDebug().isPaused());
            battleResponse.setFps(battle.getFpsCalculator().getFps());
            battle.getQueues().getBattleUpdatesQueue().add(battleResponse);
        }
    }

    private boolean sendBattleStateToUpdatesQueue(Battle battle) {
        var sendStage = battle.isStageUpdated();
        var sendState = existsUpdatedModel(battle.getModel());
        var sendUpdates = !battle.getModel().getUpdates().isEmpty();
        var sendEvents = !battle.getModel().getEvents().isEmpty();
        if (sendStage || sendState || sendUpdates || sendEvents) {
            var battleUpdateResponse = new BattleUpdateResponse()
                    .setId(battle.getId())
                    .setTime(battle.getTime())
                    .setFps(battle.getFpsCalculator().getFps());
            if (sendStage) {
                battleUpdateResponse.setStage(battle.getBattleStage());
            } else {
                if (sendState) {
                    battleUpdateResponse.setState(createBattleModelStateResponse(battle.getModel()));
                }
                if (sendUpdates) {
                    battleUpdateResponse.setUpdates(battle.getModel().getUpdates());
                }
                if (sendEvents) {
                    battleUpdateResponse.setEvents(battle.getModel().getEvents());
                }
            }
            battle.getQueues().getBattleUpdatesQueue().add(battleUpdateResponse);
            return true;
        }
        return false;
    }

    private boolean existsUpdatedModel(BattleModel battleModel) {
        var updatedVehicles = battleModel.getVehicles().values().stream()
                .anyMatch(VehicleModel::isUpdated);
        var updatedMissiles = battleModel.getMissiles().values().stream()
                .anyMatch(missileModel -> missileModel.getUpdate().isUpdated());
        var updatedDrones = battleModel.getDrones().values().stream()
                .anyMatch(droneModel -> droneModel.getUpdate().isUpdated());
        var updatedShells = battleModel.getShells().values().stream()
                .anyMatch(ShellModel::isUpdated);
        return updatedVehicles || updatedMissiles || updatedDrones || updatedShells;
    }

    private BattleModelStateResponse createBattleModelStateResponse(BattleModel battleModel) {
        var shellStates = battleModel.getShells().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
        var vehicleStates = battleModel.getVehicles().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
        var missileStates = battleModel.getMissiles().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
        var droneStates = battleModel.getDrones().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getState()));
        return new BattleModelStateResponse()
                .setShells(shellStates)
                .setVehicles(vehicleStates)
                .setMissiles(missileStates)
                .setDrones(droneStates);
    }

    private void resetUpdatedFlags(Battle battle) {
        battle.setStageUpdated(false);
        battle.getModel().setUpdated(false);
        battle.getModel().getVehicles().values().forEach(vehicle -> vehicle.setUpdated(false));
        battle.getModel().getShells().values().forEach(shell -> shell.setUpdated(false));
        battle.getModel().getMissiles().values().forEach(missile -> missile.getUpdate().resetUpdated());
        battle.getModel().getDrones().values().forEach(drone -> drone.getUpdate().resetUpdated());
    }
}
