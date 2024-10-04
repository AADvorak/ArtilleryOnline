package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class EngineAccelerationCalculator {

    public static void calculate(WheelCalculations wheelCalculations, VehicleModel vehicleModel) {
        if (vehicleModel.getState().getTrackState().isBroken()
                || WheelGroundState.FULL_UNDER_GROUND.equals(wheelCalculations.getGroundState())
                || WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())) {
            return;
        }
        var depth = wheelCalculations.getDepth();
        var groundAngle = wheelCalculations.getGroundAngle();
        var direction = vehicleModel.getState().getMovingDirection();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var depthCoefficient = 1 - depth * 0.5 / wheelRadius;
        var acceleration = depthCoefficient * vehicleModel.getSpecs().getAcceleration() / 2;
        var depthAngle = depth * Math.PI / (4 * wheelRadius);
        if (MovingDirection.RIGHT.equals(direction)) {
            wheelCalculations.getEngineAcceleration()
                    .setX(acceleration * Math.cos(groundAngle + depthAngle))
                    .setY(acceleration * Math.sin(groundAngle + depthAngle));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            wheelCalculations.getEngineAcceleration()
                    .setX( - acceleration * Math.cos(groundAngle - depthAngle))
                    .setY( - acceleration * Math.sin(groundAngle - depthAngle));
        }
    }
}
