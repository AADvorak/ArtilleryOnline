package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;

public class GravityAccelerationCalculator {

    public static void calculate(WheelCalculations wheelCalculations, double roomGravityAcceleration,
                                 double groundMaxDepth) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())) {
            wheelCalculations.getGravityAcceleration()
                    .setX(0.0)
                    .setY(-roomGravityAcceleration);
        } else if (WheelGroundState.HALF_OVER_GROUND.equals(wheelCalculations.getGroundState())
                && wheelCalculations.getGroundDepth() <= groundMaxDepth) {
            var groundAngle = wheelCalculations.getGroundAngle();
            var groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle));
            wheelCalculations.getGravityAcceleration()
                    .setX(-groundAccelerationModule * Math.sin(groundAngle))
                    .setY(-groundAccelerationModule * Math.cos(groundAngle));
        }
    }
}
