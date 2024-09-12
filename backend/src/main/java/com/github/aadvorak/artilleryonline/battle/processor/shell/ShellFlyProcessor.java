package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.List;

public class ShellFlyProcessor {

    public static void processStep(ShellModel shellModel, BattleModel battleModel, List<Integer> shellIdsToRemove) {
        var prevPosition = shellModel.getState().getPosition();
        var velocity = shellModel.getState().getVelocity();
        var angle = shellModel.getState().getAngle();
        var velocityX = velocity * Math.cos(angle);
        var velocityY = velocity * Math.sin(angle);
        var nextPosition = new Position()
                .setX(prevPosition.getX() + velocityX * battleModel.getCurrentTimeStepSecs())
                .setY(prevPosition.getY() + velocityY * battleModel.getCurrentTimeStepSecs());
        if (positionIsOutOfRoom(nextPosition, battleModel.getRoom().getSpecs())) {
            shellIdsToRemove.add(shellModel.getId());
            return;
        }
        var hitVehicle = getHitVehicle(nextPosition, battleModel);
        if (hitVehicle != null) {
            ShellDamageProcessor.process(hitVehicle, shellModel.getSpecs(), battleModel);
            shellIdsToRemove.add(shellModel.getId());
            return;
        }
        if (isHitGround(nextPosition, battleModel)) {
            shellIdsToRemove.add(shellModel.getId());
            return;
        }
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        velocityY = velocityY - gravityAcceleration * battleModel.getCurrentTimeStepSecs();
        velocity = Math.sqrt(Math.pow(velocityX, 2.0) + Math.pow(velocityY, 2.0));
        angle = Math.atan(velocityY / velocityX) + (velocityX < 0 ? Math.PI : 0.0);
        shellModel.getState().setPosition(nextPosition);
        shellModel.getState().setVelocity(velocity);
        shellModel.getState().setAngle(angle);
    }

    private static VehicleModel getHitVehicle(Position nextPosition, BattleModel battleModel) {
        for (var vehicleModel : battleModel.getVehicles().values()) {
            if (positionIsInVehicle(nextPosition, vehicleModel)) {
                return vehicleModel;
            }
        }
        return null;
    }

    private static boolean positionIsInVehicle(Position position, VehicleModel vehicleModel) {
        var vehiclePosition = vehicleModel.getState().getPosition();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        // todo logic with vehicle angle
        var distanceToVehicleCenter = position.distanceTo(vehiclePosition);
        return position.getY() > vehiclePosition.getY() && distanceToVehicleCenter <= vehicleRadius;
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
