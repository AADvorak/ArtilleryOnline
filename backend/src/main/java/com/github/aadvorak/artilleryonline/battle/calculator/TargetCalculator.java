package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

import java.util.Set;
import java.util.stream.Collectors;

public class TargetCalculator {

    public static Set<Position> calculatePositions(Integer vehicleId, BattleModel battleModel) {
        var positions = battleModel.getVehicles().values().stream()
                .filter(vehicleModel -> vehicleId == null || vehicleModel.getId() != vehicleId)
                .map(vehicleModel -> vehicleModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
        if (!positions.isEmpty()) {
            return positions;
        }
        positions = battleModel.getDrones().values().stream()
                .filter(droneModel -> droneModel.getVehicleId() == null || !droneModel.getVehicleId().equals(vehicleId))
                .map(droneModel -> droneModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
        if (!positions.isEmpty()) {
            return positions;
        }
        return battleModel.getMissiles().values().stream()
                .filter(missileModel -> vehicleId == null || missileModel.getVehicleId() != vehicleId)
                .map(missileModel -> missileModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
    }
}
