package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class ShellFlyProcessor {

    public static void processStep(ShellModel shellModel, BattleModel battleModel) {
        var prevPosition = shellModel.getState().getPosition();
        var velocity = shellModel.getState().getVelocity();
        var angle = shellModel.getState().getAngle();
        var velocityX = velocity * Math.cos(angle);
        var velocityY = velocity * Math.sin(angle);
        var nextPosition = new Position()
                .setX(prevPosition.getX() + velocityX * battleModel.getCurrentTimeStepSecs())
                .setY(prevPosition.getY() + velocityY * battleModel.getCurrentTimeStepSecs());
        shellModel.getState().setPosition(nextPosition);
        if (positionIsOutOfRoom(nextPosition, battleModel.getRoom().getSpecs())) {
            battleModel.getUpdates().removeShell(shellModel.getId());
            return;
        }
        var hitTrackVehicle = getHitTrack(prevPosition, nextPosition, battleModel);
        if (hitTrackVehicle != null) {
            ShellDamageProcessor.processHitTrack(nextPosition, hitTrackVehicle, shellModel.getSpecs(), battleModel);
            battleModel.getUpdates().removeShell(shellModel.getId());
            return;
        }
        var hitVehicle = getHitVehicle(prevPosition, nextPosition, battleModel);
        if (hitVehicle != null) {
            ShellDamageProcessor.processHitVehicle(nextPosition, hitVehicle, shellModel, battleModel);
            battleModel.getUpdates().removeShell(shellModel.getId());
            return;
        }
        if (isHitGround(nextPosition, battleModel)) {
            ShellDamageProcessor.processHitGround(nextPosition, shellModel.getSpecs(), battleModel);
            battleModel.getUpdates().removeShell(shellModel.getId());
            return;
        }
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        velocityY = velocityY - gravityAcceleration * battleModel.getCurrentTimeStepSecs();
        velocity = Math.sqrt(Math.pow(velocityX, 2.0) + Math.pow(velocityY, 2.0));
        angle = Math.atan(velocityY / velocityX) + (velocityX < 0 ? Math.PI : 0.0);
        shellModel.getState().setVelocity(velocity);
        shellModel.getState().setAngle(angle);
    }

    private static VehicleModel getHitVehicle(Position prevPosition, Position nextPosition, BattleModel battleModel) {
        for (var vehicleModel : battleModel.getVehicles().values()) {
            if (isHitVehicle(prevPosition, nextPosition, vehicleModel)) {
                return vehicleModel;
            }
        }
        return null;
    }

    private static VehicleModel getHitTrack(Position prevPosition, Position nextPosition, BattleModel battleModel) {
        for (var vehicleModel : battleModel.getVehicles().values()) {
            if (isHitTrack(prevPosition, nextPosition, vehicleModel)) {
                return vehicleModel;
            }
        }
        return null;
    }

    private static boolean isHitVehicle(Position prevPosition, Position nextPosition, VehicleModel vehicleModel) {
        // todo logic with vehicle angle
        var vehiclePosition = vehicleModel.getState().getPosition();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        return nextPosition.distanceTo(vehiclePosition) <= vehicleRadius
                || BattleUtils.isLineCrossingCircle(prevPosition, nextPosition, vehiclePosition, vehicleRadius);
    }

    private static boolean isHitTrack(Position prevPosition, Position nextPosition, VehicleModel vehicleModel) {
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel);
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        return nextPosition.distanceTo(rightWheelPosition) <= wheelRadius
                || nextPosition.distanceTo(leftWheelPosition) <= wheelRadius
                || BattleUtils.isLineCrossingCircle(prevPosition, nextPosition, rightWheelPosition, wheelRadius)
                || BattleUtils.isLineCrossingCircle(prevPosition, nextPosition, leftWheelPosition, wheelRadius);
    }

    private static boolean positionIsOutOfRoom(Position position, RoomSpecs roomSpecs) {
        var xMax = roomSpecs.getRightTop().getX();
        var xMin = roomSpecs.getLeftBottom().getX();
        var yMin = roomSpecs.getLeftBottom().getY();
        return position.getX() >= xMax || position.getX() <= xMin || position.getY() <= yMin;
    }

    private static boolean isHitGround(Position position, BattleModel battleModel) {
        var nearestGroundPosition = BattleUtils.getNearestGroundPosition(position.getX(), battleModel.getRoom());
        return position.getY() <= nearestGroundPosition.getY();
    }
}
