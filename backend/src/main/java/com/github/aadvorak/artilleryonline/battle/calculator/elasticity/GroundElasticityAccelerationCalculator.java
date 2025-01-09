package com.github.aadvorak.artilleryonline.battle.calculator.elasticity;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.HashSet;
import java.util.Set;

public class GroundElasticityAccelerationCalculator {

    public static void calculate(VehicleCalculations vehicle, BattleCalculations battle) {
        getGroundCollideWheels(vehicle, battle)
                .forEach(GroundElasticityAccelerationCalculator::calculateElasticityAcceleration);
    }

    private static Set<WheelCalculations> getGroundCollideWheels(VehicleCalculations vehicle, BattleCalculations battle) {
        var groundCollideWheels = new HashSet<WheelCalculations>();
        var rightWheelPosition = vehicle.getRightWheel().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getPosition();
        var rightWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(rightWheelPosition.getX(),
                battle.getModel().getRoom());
        var leftWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(leftWheelPosition.getX(),
                battle.getModel().getRoom());
        if (vehicle.getRightWheel().getNearestGroundPoint() != null
                && rightWheelNearestGroundPoint.getY() >= rightWheelPosition.getY()) {
            groundCollideWheels.add(vehicle.getRightWheel());
        }
        if (vehicle.getLeftWheel().getNearestGroundPoint() != null
                && leftWheelNearestGroundPoint.getY() >= leftWheelPosition.getY()) {
            groundCollideWheels.add(vehicle.getLeftWheel());
        }
        return groundCollideWheels;
    }

    private static void calculateElasticityAcceleration(WheelCalculations wheelCalculations) {
        var coefficient = 100.0;
        var groundAngle = wheelCalculations.getGroundAngle();
        var acceleration = new VectorProjections(groundAngle).setNormal(coefficient).recoverAcceleration();
        wheelCalculations.getGroundElasticityAcceleration().add(acceleration);
    }
}
