package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleGravityAccelerationUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        if (vehicleModel.isCollided()) {
            vehicleModel.setCollided(false);
        } else {
            recalculateVelocity(vehicleModel, battleModel);
        }
        var nextPosition = getNextVehiclePosition(vehicleModel, battleModel);
        if (wallCollide(vehicleModel, battleModel, nextPosition)) {
            vehicleModel.getState().setVelocity(- vehicleModel.getState().getVelocity() / 2);
            return;
        }
        var vehicleCollide = vehicleCollide(vehicleModel, battleModel, nextPosition);
        if (vehicleCollide != null) {
            doCollide(vehicleModel, vehicleCollide);
            return;
        }
        doMoveStep(vehicleModel, battleModel, nextPosition);
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

    private static VehicleModel vehicleCollide(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var velocity = vehicleModel.getState().getVelocity();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition);
        for (var otherVehicleModel : otherVehicleModels) {
            var otherVehiclePosition = otherVehicleModel.getState().getPosition();
            var otherWheelRadius = otherVehicleModel.getSpecs().getWheelRadius();
            var minDistance = wheelRadius + otherWheelRadius;
            if (velocity > 0) {
                var otherLeftWheelPosition = VehicleUtils.getLeftWheelPosition(otherVehicleModel, otherVehiclePosition);
                var distance = otherLeftWheelPosition.distanceTo(rightWheelPosition);
                if (distance < minDistance) {
                    return otherVehicleModel;
                }
            }
            if (velocity < 0) {
                var otherRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, otherVehiclePosition);
                var distance = otherRightWheelPosition.distanceTo(leftWheelPosition);
                if (distance < minDistance) {
                    return otherVehicleModel;
                }
            }
        }
        return null;
    }

    private static void doMoveStep(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        vehicleModel.getState().setPosition(nextPosition);
        VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
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

    private static void doCollide(VehicleModel vehicle, VehicleModel otherVehicle) {
        var vehicleVelocity = vehicle.getState().getVelocity();
        var otherVehicleVelocity = otherVehicle.getState().getVelocity();
        if (vehicleVelocity * otherVehicleVelocity > 0) {
            vehicle.getState().setVelocity(otherVehicleVelocity);
            otherVehicle.getState().setVelocity(vehicleVelocity);
        } else {
            vehicle.getState().setVelocity(otherVehicleVelocity / 2);
            otherVehicle.getState().setVelocity(vehicleVelocity / 2);
        }
        otherVehicle.setCollided(true);
    }
}
