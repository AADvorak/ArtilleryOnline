package com.github.aadvorak.artilleryonline.battle.calculator.elasticity;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Acceleration;

public class WallElasticityAccelerationCalculator {

    public static void calculate(VehicleCalculations vehicle, BattleCalculations battle) {
        var coefficient = 100.0;
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var rightWheelPosition = vehicle.getRightWheel().getPosition();

        if (rightWheelPosition.getX() + wheelRadius >= xMax) {
            vehicle.getWallElasticityAcceleration().add(new Acceleration().setX(-coefficient));
            return;
        }
        var leftWheelPosition = vehicle.getLeftWheel().getPosition();
        if (leftWheelPosition.getX() - wheelRadius <= xMin) {
            vehicle.getWallElasticityAcceleration().add(new Acceleration().setX(coefficient));
        }
    }
}
