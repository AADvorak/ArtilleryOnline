package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        if (vehicleModel.getState().getMovingDirection() == null) {
            return;
        }
        var nextPosition = getNextVehiclePosition(vehicleModel, battleModel);
        if (!canMove(vehicleModel, battleModel, nextPosition)) {
            battleModel.setUpdated(true);
            return;
        }
        doMoveStep(vehicleModel, battleModel, nextPosition);
    }

    private static boolean canMove(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        if (vehicleModel.getState().getTrackState().isBroken()) {
            return false;
        }
        if (wallCollide(vehicleModel, battleModel, nextPosition)) {
            return false;
        }
        return !vehicleCollide(vehicleModel, battleModel, nextPosition);
    }

    private static boolean wallCollide(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        var direction = vehicleModel.getState().getMovingDirection();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        if (MovingDirection.RIGHT.equals(direction)) {
            var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition);
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (MovingDirection.LEFT.equals(direction)) {
            var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition);
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
    }

    private static boolean vehicleCollide(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var direction = vehicleModel.getState().getMovingDirection();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition);
        for (var otherVehicleModel : otherVehicleModels) {
            var otherVehiclePosition = otherVehicleModel.getState().getPosition();
            var otherWheelRadius = otherVehicleModel.getSpecs().getWheelRadius();
            var minDistance = wheelRadius + otherWheelRadius;
            if (MovingDirection.RIGHT.equals(direction)) {
                var otherLeftWheelPosition = VehicleUtils.getLeftWheelPosition(otherVehicleModel, otherVehiclePosition);
                var distance = otherLeftWheelPosition.distanceTo(rightWheelPosition);
                if (distance < minDistance) {
                    return true;
                }
            }
            if (MovingDirection.LEFT.equals(direction)) {
                var otherRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, otherVehiclePosition);
                var distance = otherRightWheelPosition.distanceTo(leftWheelPosition);
                if (distance < minDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void doMoveStep(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        vehicleModel.getState().setPosition(nextPosition);
        VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
    }

    private static Position getNextVehiclePosition(VehicleModel vehicleModel, BattleModel battleModel) {
        var position = vehicleModel.getState().getPosition();
        var angle = vehicleModel.getState().getAngle();
        var velocity = getVelocity(vehicleModel);
        var velocityX = velocity * Math.cos(angle);
        var velocityY = velocity * Math.sin(angle);
        var nextX = position.getX() + velocityX * battleModel.getCurrentTimeStepSecs();
        var nextY = position.getY() + velocityY * battleModel.getCurrentTimeStepSecs();
        return new Position().setX(nextX).setY(nextY);
    }

    private static double getVelocity(VehicleModel vehicleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        var velocity = vehicleModel.getSpecs().getMovingVelocity();
        var angle = vehicleModel.getState().getAngle();
        var absAngle = Math.abs(angle);
        var criticalAngle = vehicleModel.getSpecs().getCriticalAngle();
        if (MovingDirection.RIGHT.equals(direction)) {
            if (angle > criticalAngle) {
                return 0;
            } else if (angle > 0) {
                return velocity * (criticalAngle - absAngle) / criticalAngle;
            } else if (angle > -criticalAngle) {
                return velocity * (criticalAngle + absAngle) / criticalAngle;
            } else {
                return 2 * velocity;
            }
        }
        if (MovingDirection.LEFT.equals(direction)) {
            if (angle < -criticalAngle) {
                return 0;
            } else if (angle < 0) {
                return - velocity * (criticalAngle - absAngle) / criticalAngle;
            } else if (angle < criticalAngle) {
                return - velocity * (criticalAngle + absAngle) / criticalAngle;
            } else {
                return - 2 * velocity;
            }
        }
        return 0;
    }
}
