package com.github.aadvorak.artilleryonline.battle.calculator.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

import java.util.stream.Collectors;

public class CorrectingAccelerationCalculator {

    public static double calculate(MissileCalculations calculations, BattleModel battleModel) {
        var missileState = calculations.getModel().getState();
        var correctingVelocity = missileState.getVelocity().getMovingVelocity().magnitude()
                - calculations.getModel().getSpecs().getMinCorrectingVelocity();
        if (correctingVelocity <= 0) {
            return 0.0;
        }
        var targets = battleModel.getVehicles().values().stream()
                .filter(vehicleModel -> vehicleModel.getId() != calculations.getModel().getVehicleId())
                .collect(Collectors.toSet());
        if (targets.isEmpty()) {
            return 0.0;
        }
        var angleDiffs = targets.stream()
                .map(vehicleModel -> {
                    var missilePosition = missileState.getPosition();
                    return calculateAngleDiff(missilePosition.getAngle(),
                            missilePosition.getCenter().angleTo(vehicleModel.getState().getPosition()));
                })
                .collect(Collectors.toSet());
        var iterator = angleDiffs.iterator();
        var minAngleDiff = iterator.next();
        while (iterator.hasNext()) {
            var angleDiff = iterator.next();
            if (Math.abs(angleDiff) < Math.abs(minAngleDiff)) {
                minAngleDiff = angleDiff;
            }
        }

        return - Math.signum(minAngleDiff) * correctingVelocity
                * calculations.getModel().getSpecs().getCorrectingAccelerationCoefficient();
    }

    private static double calculateAngleDiff(double missileAngle, double targetAngle) {
        double diff = missileAngle - targetAngle;
        if (Math.abs(diff) > Math.PI) {
            if (diff > 0) {
                return 2 * Math.PI - diff;
            } else {
                return 2 * Math.PI + diff;
            }
        } else {
            return diff;
        }
    }
}
