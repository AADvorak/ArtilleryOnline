package com.github.aadvorak.artilleryonline.battle.calculator.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.stream.Collectors;

public class CorrectingAccelerationCalculator {

    public static double calculate(MissileCalculations calculations, BattleModel battleModel) {
        var missileState = calculations.getModel().getState();
        var missileSpecs = calculations.getModel().getSpecs();
        var velocityMagnitude = missileState.getVelocity().getMovingVelocity().magnitude();
        var correctingVelocity = velocityMagnitude - missileSpecs.getMinCorrectingVelocity();
        var missilePosition = missileState.getPosition();
        if (correctingVelocity <= 0) {
            var verticalAngleDiff = GeometryUtils.calculateAngleDiff(missilePosition.getAngle(), Math.PI / 2);
            if (Math.abs(verticalAngleDiff) < missileSpecs.getAnglePrecision()) {
                return 0.0;
            }
            return Math.signum(verticalAngleDiff) * velocityMagnitude
                    * missileSpecs.getCorrectingAccelerationCoefficient() / missileSpecs.getMinCorrectingVelocity();
        }
        var targets = battleModel.getVehicles().values().stream()
                .filter(vehicleModel -> vehicleModel.getId() != calculations.getModel().getVehicleId())
                .collect(Collectors.toSet());
        if (targets.isEmpty()) {
            return 0.0;
        }
        var angleDiffs = targets.stream()
                .map(vehicleModel -> GeometryUtils.calculateAngleDiff(missilePosition.getAngle(),
                        missilePosition.getCenter().angleTo(vehicleModel.getState().getPosition().getCenter())))
                .collect(Collectors.toSet());
        var iterator = angleDiffs.iterator();
        var minAngleDiff = iterator.next();
        while (iterator.hasNext()) {
            var angleDiff = iterator.next();
            if (Math.abs(angleDiff) < Math.abs(minAngleDiff)) {
                minAngleDiff = angleDiff;
            }
        }
        if (Math.abs(minAngleDiff) < missileSpecs.getAnglePrecision()) {
            return 0.0;
        }
        return Math.signum(minAngleDiff) * correctingVelocity * missileSpecs.getCorrectingAccelerationCoefficient();
    }
}
