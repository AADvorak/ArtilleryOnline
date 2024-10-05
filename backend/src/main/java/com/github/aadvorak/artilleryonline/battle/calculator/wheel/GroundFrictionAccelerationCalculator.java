package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class GroundFrictionAccelerationCalculator {

    public static void calculate(WheelCalculations wheelCalculations, VehicleModel vehicleModel,
                                 double groundFrictionCoefficient) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())) {
            return;
        }
        var depth = WheelGroundState.FULL_UNDER_GROUND.equals(wheelCalculations.getGroundState())
                ? 2 * vehicleModel.getSpecs().getWheelRadius() : wheelCalculations.getGroundDepth();
        wheelCalculations.getGroundFrictionAcceleration()
                .setX( - wheelCalculations.getVelocity().getX() * depth * groundFrictionCoefficient)
                .setY( - wheelCalculations.getVelocity().getY() * depth * groundFrictionCoefficient);
    }
}
