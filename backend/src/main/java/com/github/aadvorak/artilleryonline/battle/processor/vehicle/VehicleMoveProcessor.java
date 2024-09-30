package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.calculator.VehicleAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        if (!vehicleModel.isCollided()) {
            recalculateVelocity(vehicleModel, battleModel);
        }
        var nextPosition = getNextVehiclePosition(vehicleModel, battleModel);
        var nextAngle = getNextVehicleAngle(vehicleModel, battleModel);
        if (wallCollide(vehicleModel, battleModel, nextPosition, nextAngle)) {
            vehicleModel.getState().getVehicleVelocity().setX(
                    - vehicleModel.getState().getVehicleVelocity().getX() / 2);
            battleModel.setUpdated(true);
            return;
        }
        if (VehicleCollideProcessor.processCollide(vehicleModel, battleModel, nextPosition, nextAngle)) {
            return;
        }
        if (VehicleGroundCollideProcessor.processCollide(vehicleModel, battleModel, nextPosition, nextAngle)) {
            return;
        }
        vehicleModel.getState().setPosition(nextPosition);
        vehicleModel.getState().setAngle(nextAngle);
        vehicleModel.setCollided(false);
    }

    private static void recalculateVelocity(VehicleModel vehicleModel, BattleModel battleModel) {
        var acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(vehicleModel, battleModel.getRoom());
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        vehicleVelocity.setX(vehicleVelocity.getX() + acceleration.getX() * battleModel.getCurrentTimeStepSecs());
        vehicleVelocity.setY(vehicleVelocity.getY() + acceleration.getY() * battleModel.getCurrentTimeStepSecs());
        vehicleVelocity.setAngle(vehicleVelocity.getAngle() + acceleration.getAngle() * battleModel.getCurrentTimeStepSecs());
    }

    private static boolean wallCollide(VehicleModel vehicleModel, BattleModel battleModel,
                                       Position nextPosition, double nextAngle) {
        var velocityX = vehicleModel.getState().getVehicleVelocity().getX();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        if (velocityX > 0) {
            var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition, nextAngle);
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (velocityX < 0) {
            var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition, nextAngle);
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
    }

    private static Position getNextVehiclePosition(VehicleModel vehicleModel, BattleModel battleModel) {
        var position = vehicleModel.getState().getPosition();
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        var nextX = position.getX() + vehicleVelocity.getX() * battleModel.getCurrentTimeStepSecs();
        var nextY = position.getY() + vehicleVelocity.getY() * battleModel.getCurrentTimeStepSecs();
        return new Position().setX(nextX).setY(nextY);
    }

    private static double getNextVehicleAngle(VehicleModel vehicleModel, BattleModel battleModel) {
        var angle = vehicleModel.getState().getAngle();
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        return angle + vehicleVelocity.getAngle() * battleModel.getCurrentTimeStepSecs();
    }
}
