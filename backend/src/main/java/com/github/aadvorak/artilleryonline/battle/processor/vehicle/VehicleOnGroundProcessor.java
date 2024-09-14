package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class VehicleOnGroundProcessor {

    public static void correctVehiclePositionAndAngleOnGround(VehicleModel vehicleModel, RoomModel roomModel) {
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var angle = vehicleModel.getState().getAngle();
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var wheelAngle = vehicleModel.getPreCalc().getWheelAngle();
        var leftWheelX = vehicleModel.getState().getPosition().getX() - wheelDistance * Math.cos(wheelAngle + angle);
        var leftWheelPosition = getLeftWheelPositionOnGround(leftWheelX, wheelRadius, roomModel);
        angle = getVehicleAngleOnGround(leftWheelPosition, vehicleRadius, wheelRadius, roomModel);
        var position = new Position()
                .setX(leftWheelPosition.getX() + wheelDistance * Math.cos(wheelAngle + angle))
                .setY(leftWheelPosition.getY() + wheelDistance * Math.sin(wheelAngle + angle));
        vehicleModel.getState()
                .setPosition(position)
                .setAngle(angle);
    }

    private static Position getLeftWheelPositionOnGround(double x, double wheelRadius, RoomModel roomModel) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(x - wheelRadius, x + wheelRadius, roomModel);
        var groundPosition = BattleUtils.getGroundPosition(groundIndexes.get(0), roomModel);
        var wheelPosition = new Position()
                .setX(x)
                .setY(getLeftWheelY(x, wheelRadius, groundPosition));
        for (var i = 1; i < groundIndexes.size(); i++) {
            groundPosition = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            if (groundPosition.distanceTo(wheelPosition) < wheelRadius) {
                wheelPosition.setY(getLeftWheelY(x, wheelRadius, groundPosition));
            }
        }
        return wheelPosition;
    }

    private static double getLeftWheelY(double x, double wheelRadius, Position groundPosition) {
        return groundPosition.getY() + Math.sqrt(Math.pow(wheelRadius, 2) - Math.pow(x - groundPosition.getX(), 2));
    }

    private static double getVehicleAngleOnGround(Position leftWheelPosition, double vehicleRadius,
                                                 double wheelRadius, RoomModel roomModel) {
        var groundPositions = BattleUtils.getGroundIndexesBetween(leftWheelPosition.getX(),
                leftWheelPosition.getX() + 2 * vehicleRadius + wheelRadius, roomModel).stream()
                .map(groundIndex -> BattleUtils.getGroundPosition(groundIndex, roomModel))
                .filter(groundPosition -> groundPosition.distanceTo(leftWheelPosition) > 2 * vehicleRadius - wheelRadius)
                .toList();
        // todo bug fix
        var vehicleAngle = getVehicleAngleOnGround(leftWheelPosition, groundPositions.get(0), vehicleRadius, wheelRadius);
        for (var i = 1; i < groundPositions.size(); i++) {
            var angle = getVehicleAngleOnGround(leftWheelPosition, groundPositions.get(i), vehicleRadius, wheelRadius);
            if (angle > vehicleAngle) {
                vehicleAngle = angle;
            }
        }
        return vehicleAngle;
    }

    private static double getVehicleAngleOnGround(Position leftWheelPosition, Position groundPosition,
                                                 double vehicleRadius, double wheelRadius) {
        var xDiff = leftWheelPosition.getX() - groundPosition.getX();
        var yDiff = leftWheelPosition.getY() - groundPosition.getY();
        var sumCoefficient = (Math.pow(wheelRadius, 2) - Math.pow(xDiff, 2) - Math.pow(yDiff, 2)
                - Math.pow(2 * vehicleRadius, 2)) / (4 * vehicleRadius);
        var multiplierCoefficient = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
        return Math.asin(sumCoefficient / multiplierCoefficient) - Math.asin(xDiff / multiplierCoefficient);
    }
}
