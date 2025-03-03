package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

import java.util.Set;
import java.util.stream.Collectors;

public class TargetCalculator {

    public static Set<Position> calculatePositions(int vehicleId, BattleModel battleModel) {
        var positions = battleModel.getVehicles().values().stream()
                .filter(vehicleModel -> vehicleModel.getId() != vehicleId)
                .map(vehicleModel -> vehicleModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
        if (!positions.isEmpty()) {
            return positions;
        }
        positions = battleModel.getDrones().values().stream()
                .filter(droneModel -> droneModel.getVehicleId() == null || droneModel.getVehicleId() != vehicleId)
                .map(droneModel -> droneModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
        if (!positions.isEmpty()) {
            return positions;
        }
        return battleModel.getMissiles().values().stream()
                .filter(missileModel -> missileModel.getVehicleId() != vehicleId)
                .map(missileModel -> missileModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
    }
}
