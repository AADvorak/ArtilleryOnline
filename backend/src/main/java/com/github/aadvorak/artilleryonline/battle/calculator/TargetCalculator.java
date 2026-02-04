package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;

import java.util.Set;
import java.util.stream.Collectors;

public class TargetCalculator {

    public static Set<Position> calculatePositions(Integer vehicleId, BattleCalculations battle) {
        var battleModel = battle.getModel();
        var positions = battleModel.getVehicles().values().stream()
                .filter(vehicleModel -> vehicleId == null || battle.allowedTarget(vehicleModel.getId(), vehicleId))
                .map(vehicleModel -> vehicleModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
        if (!positions.isEmpty()) {
            return positions;
        }
        positions = battleModel.getDrones().values().stream()
                .filter(droneModel -> droneModel.getVehicleId() == null || vehicleId == null
                        || battle.allowedTarget(droneModel.getVehicleId(), vehicleId))
                .map(droneModel -> droneModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
        if (!positions.isEmpty()) {
            return positions;
        }
        return battleModel.getMissiles().values().stream()
                .filter(missileModel -> vehicleId == null || battle.allowedTarget(missileModel.getVehicleId(), vehicleId))
                .map(missileModel -> missileModel.getState().getPosition().getCenter())
                .collect(Collectors.toSet());
    }
}
