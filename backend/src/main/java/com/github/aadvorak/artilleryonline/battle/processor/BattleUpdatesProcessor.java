package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.updates.RoomStateUpdate;
import com.github.aadvorak.artilleryonline.dto.response.BattleModelStateResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleUpdateResponse;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BattleUpdatesProcessor {

    private final ApplicationSettings applicationSettings;

    private final ModelMapper mapper = new ModelMapper();

    public void process(Battle battle) {
        setBattleUpdatedByTimeout(battle);
        sendBattleToUpdatesQueue(battle);
        resetUpdatedFlags(battle);
    }

    public void sendBattleToUpdatesQueue(Battle battle) {
        var updatesAggregated = aggregateUpdateIfExist(battle);
        if (battle.getModel().isUpdated()
                || (battle.getDebug().isForceSend()
                || (!applicationSettings.isClientProcessing() && !battle.getDebug().isPaused())
                && !updatesAggregated)) {
            var battleResponse = mapper.map(battle, BattleResponse.class);
            battleResponse.setPaused(battle.getDebug().isPaused());
            battleResponse.setFps(battle.getFpsCalculator().getFps());
            battle.getQueues().getBattleUpdatesQueue().add(battleResponse);
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
        for (var vehicleModel: battleModel.getVehicles().values()) {
            if (vehicleModel.getUpdate().setUpdatedByTimeout(battle.getAbsoluteTime())) {
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
        battleModel.getVehicles().values().forEach(model ->
                model.getUpdate().setUpdated(battle.getAbsoluteTime()));
    }

    private boolean aggregateUpdateIfExist(Battle battle) {
        var sendStage = battle.isStageUpdated();
        var sendState = existsUpdatedModel(battle.getModel()) || !applicationSettings.isClientProcessing();
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
            aggregateUpdates(battle, battleUpdateResponse);
            return true;
        }
        return false;
    }

    private void aggregateUpdates(Battle battle, BattleUpdateResponse update) {
        synchronized (battle.getAggregated()) {
            var aggregatedUpdate = battle.getAggregated().getUpdate();
            if (aggregatedUpdate == null) {
                battle.getAggregated().setUpdate(update);
            } else {
                aggregatedUpdate.setTime(update.getTime());
                aggregatedUpdate.setFps((update.getFps() + aggregatedUpdate.getFps()) / 2);
                if (update.getStage() != null) {
                    aggregatedUpdate.setStage(update.getStage());
                }
                if (update.getState() != null) {
                    aggregatedUpdate.setState(update.getState());
                }
                if (update.getUpdates() != null) {
                    if (aggregatedUpdate.getUpdates() == null) {
                        aggregatedUpdate.setUpdates(update.getUpdates());
                    } else {
                        aggregatedUpdate.getUpdates().merge(update.getUpdates());
                    }
                }
                if (update.getEvents() != null) {
                    if (aggregatedUpdate.getEvents() == null) {
                        aggregatedUpdate.setEvents(update.getEvents());
                    } else {
                        aggregatedUpdate.getEvents().merge(update.getEvents());
                    }
                }
            }
            if (battle.getAggregated().getUpdate().getUpdates() != null) {
                mergeIntersectedRoomStateUpdates(battle);
            }
        }
    }

    private void mergeIntersectedRoomStateUpdates(Battle battle) {
        var roomStateUpdates = battle.getAggregated().getUpdate().getUpdates().getRoomStateUpdates();
        if (roomStateUpdates != null && roomStateUpdates.size() > 1) {
            var groundLine = battle.getModel().getRoom().getState().getGroundLine();
            roomStateUpdates.sort(Comparator.comparingInt(RoomStateUpdate::getBegin));
            var merged = new ArrayList<RoomStateUpdate>();
            var current = roomStateUpdates.get(0);
            for (int i = 1; i < roomStateUpdates.size(); i++) {
                var next = roomStateUpdates.get(i);
                if (current.intersects(next)) {
                    current = RoomStateUpdate.of(
                            groundLine,
                            Math.min(current.getBegin(), next.getBegin()),
                            Math.max(current.getEnd(), next.getEnd())
                    );
                } else {
                    merged.add(current);
                    current = next;
                }
            }
            merged.add(current);
            battle.getAggregated().getUpdate().getUpdates().setRoomStateUpdates(merged);
        }
    }

    private boolean existsUpdatedModel(BattleModel battleModel) {
        var updatedVehicles = battleModel.getVehicles().values().stream()
                .anyMatch(vehicleModel -> vehicleModel.getUpdate().isUpdated());
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
        battle.getModel().getVehicles().values().forEach(vehicle -> vehicle.getUpdate().resetUpdated());
        battle.getModel().getShells().values().forEach(shell -> shell.setUpdated(false));
        battle.getModel().getMissiles().values().forEach(missile -> missile.getUpdate().resetUpdated());
        battle.getModel().getDrones().values().forEach(drone -> drone.getUpdate().resetUpdated());
    }
}
