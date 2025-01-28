package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class GroundReactionAccelerationCalculator {

    public static void calculate(WheelCalculations wheelCalculations, VehicleModel vehicleModel,
                                 double groundReactionCoefficient) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())
                || WheelGroundState.FULL_UNDER_GROUND.equals(wheelCalculations.getGroundState())) {
            return;
        }
        var groundAngle = wheelCalculations.getGroundAngle();
        var velocityNormalProjection = wheelCalculations.getVelocity().projections(groundAngle).getNormal();
        if (velocityNormalProjection < 0) {
            var mass = vehicleModel.getPreCalc().getMass();
            var accelerationProjections = new VectorProjections(groundAngle)
                    .setNormal(- velocityNormalProjection * wheelCalculations.getGroundDepth()
                            * groundReactionCoefficient / mass);
            wheelCalculations.setGroundReactionAcceleration(accelerationProjections.recoverAcceleration());
        }
    }
}
