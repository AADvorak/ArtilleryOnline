package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;

public class GroundReactionAccelerationCalculator {

    public static void calculate(WheelCalculations wheelCalculations, double groundReactionCoefficient) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())
                || WheelGroundState.FULL_UNDER_GROUND.equals(wheelCalculations.getGroundState())) {
            return;
        }
        var groundAngle = wheelCalculations.getGroundAngle();
        var velocityVerticalProjection = VectorUtils.getVerticalProjection(wheelCalculations.getVelocity(), groundAngle);
        if (velocityVerticalProjection < 0) {
            var accelerationVerticalProjection = - velocityVerticalProjection * wheelCalculations.getDepth()
                    * groundReactionCoefficient;
            wheelCalculations.getGroundReactionAcceleration()
                    .setX(VectorUtils.getComponentX(accelerationVerticalProjection, 0.0, groundAngle))
                    .setY(VectorUtils.getComponentY(accelerationVerticalProjection, 0.0, groundAngle));
        }
    }
}
