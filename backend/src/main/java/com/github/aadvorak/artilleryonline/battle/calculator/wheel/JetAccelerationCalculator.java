package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class JetAccelerationCalculator {

    public static void calculate(WheelCalculations wheelCalculations, VehicleModel vehicleModel) {
        var jetSpecs = vehicleModel.getConfig().getJet();
        if (jetSpecs == null) {
            return;
        }
        var jetState = vehicleModel.getState().getJetState();
        if (!jetState.isActive() || jetState.getVolume() <= 0) {
            return;
        }
        var acceleration = jetSpecs.getAcceleration();
        var direction = vehicleModel.getState().getMovingDirection();
        var angle = vehicleModel.getState().getAngle();
        var angleCoefficient = 1 + wheelCalculations.getSign().getValue() * Math.sin(angle);
        if (direction == null) {
            wheelCalculations.getJetAcceleration()
                    .setX(0.0)
                    .setY(acceleration * angleCoefficient);
        }
        if (MovingDirection.RIGHT.equals(direction)) {
            wheelCalculations.getJetAcceleration()
                    .setX(acceleration / Math.sqrt(2))
                    .setY(acceleration * angleCoefficient / Math.sqrt(2));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            wheelCalculations.getJetAcceleration()
                    .setX(- acceleration / Math.sqrt(2))
                    .setY(acceleration * angleCoefficient / Math.sqrt(2));
        }
    }
}
