package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleGravityAccelerationUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        if (!vehicleModel.isCollided()) {
            recalculateVelocity(vehicleModel, battleModel);
        }
        var nextPosition = getNextVehiclePosition(vehicleModel, battleModel);
        if (wallCollide(vehicleModel, battleModel, nextPosition)) {
            vehicleModel.getState().setVelocity(- vehicleModel.getState().getVelocity() / 2);
            battleModel.setUpdated(true);
            return;
        }
        if (VehicleCollideProcessor.processCollide(vehicleModel, battleModel, nextPosition)) {
            return;
        }
        doMoveStep(vehicleModel, battleModel, nextPosition);
        vehicleModel.setCollided(false);
    }

    private static void recalculateVelocity(VehicleModel vehicleModel, BattleModel battleModel) {
        var acceleration = VehicleUtils.getVehicleAcceleration(vehicleModel)
                + VehicleGravityAccelerationUtils.getVehicleGravityAcceleration(vehicleModel, battleModel.getRoom());
        vehicleModel.getState().setVelocity(vehicleModel.getState().getVelocity()
                + battleModel.getCurrentTimeStepSecs() * acceleration);
    }

    private static boolean wallCollide(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        var velocity = vehicleModel.getState().getVelocity();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        if (velocity > 0) {
            var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition);
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (velocity < 0) {
            var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition);
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
    }

    private static void doMoveStep(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        vehicleModel.getState().setPosition(nextPosition);
        if (!vehicleModel.isCollided()) {
            VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
        }
    }

    private static Position getNextVehiclePosition(VehicleModel vehicleModel, BattleModel battleModel) {
        var position = vehicleModel.getState().getPosition();
        var angle = vehicleModel.getState().getAngle();
        var velocity = vehicleModel.getState().getVelocity();
        var velocityX = velocity * Math.cos(angle);
        var velocityY = velocity * Math.sin(angle);
        var nextX = position.getX() + velocityX * battleModel.getCurrentTimeStepSecs();
        var nextY = position.getY() + velocityY * battleModel.getCurrentTimeStepSecs();
        return new Position().setX(nextX).setY(nextY);
    }
}
