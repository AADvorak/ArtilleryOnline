package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

import java.util.stream.Collectors;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        if (vehicleModel.getState().getMovingDirection() == null
                || !canMove(vehicleModel, battleModel)) {
            return;
        }
        doMoveStep(vehicleModel);
    }

    private static boolean canMove(VehicleModel vehicleModel, BattleModel battleModel) {
        var wallCollide = wallCollide(vehicleModel, battleModel);
        var vehicleCollide = vehicleCollide(vehicleModel, battleModel);
        return !wallCollide && !vehicleCollide;
    }

    private static boolean wallCollide(VehicleModel vehicleModel, BattleModel battleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        var nextX = getNextVehiclePosition(vehicleModel).getX();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        return MovingDirection.RIGHT.equals(direction) && nextX >= xMax
                || MovingDirection.LEFT.equals(direction) && nextX <= xMin;
    }

    private static boolean vehicleCollide(VehicleModel vehicleModel, BattleModel battleModel) {
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var nextVehiclePosition = getNextVehiclePosition(vehicleModel);
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        for (var otherVehicleModel : otherVehicleModels) {
            var otherVehiclePosition = otherVehicleModel.getState().getPosition();
            var otherVehicleRadius = otherVehicleModel.getSpecs().getRadius();
            var distance = nextVehiclePosition.distanceTo(otherVehiclePosition);
            var minDistance = vehicleRadius + otherVehicleRadius;
            if (distance <= minDistance) {
                return true;
            }
        }
        return false;
    }

    private static void doMoveStep(VehicleModel vehicleModel) {
        // todo more detailed algorithm
        vehicleModel.getState().setPosition(getNextVehiclePosition(vehicleModel));
    }

    private static Position getNextVehiclePosition(VehicleModel vehicleModel) {
        var position = vehicleModel.getState().getPosition();
        var nextX = position.getX() + getVelocity(vehicleModel) * Battle.getTimeStepSecs();
        return new Position().setX(nextX).setY(position.getY());
    }

    private static double getVelocity(VehicleModel vehicleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        var velocity = vehicleModel.getSpecs().getMovingVelocity();
        if (MovingDirection.RIGHT.equals(direction)) {
            return velocity;
        }
        return -velocity;
    }
}
