package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class VehicleOnGroundProcessor {

    public static void estimateVehicleAngleByPosition(VehicleModel vehicleModel, RoomModel roomModel) {
        var x = vehicleModel.getState().getPosition().getX();
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var leftGroundPosition = BattleUtils.getNearestGroundPosition(x - wheelDistance, roomModel);
        var rightGroundPosition = BattleUtils.getNearestGroundPosition(x + wheelDistance, roomModel);
        vehicleModel.getState().getPosition().setAngle(Math.atan((rightGroundPosition.getY() - leftGroundPosition.getY())
                / 2 * wheelDistance));
    }

    public static void correctVehiclePositionAndAngleOnGround(VehicleModel vehicleModel, RoomModel roomModel) {
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var angle = vehicleModel.getState().getPosition().getAngle();
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var wheelAngle = vehicleModel.getPreCalc().getWheelAngle();
        var sign = (short) (angle > 0 ? -1 : 1);
        var firstWheelX = vehicleModel.getState().getPosition().getX()
                - sign * wheelDistance * Math.cos(wheelAngle + sign * angle);
        var firstWheelPosition = getWheelPositionOnGround(firstWheelX, wheelRadius, roomModel);
        angle = getVehicleAngleOnGround(firstWheelPosition, sign, wheelDistance, wheelRadius, roomModel);
        var position = new Position()
                .setX(firstWheelPosition.getX() + sign * wheelDistance * Math.cos(sign * wheelAngle + angle))
                .setY(firstWheelPosition.getY() + sign * wheelDistance * Math.sin(sign * wheelAngle + angle));
        vehicleModel.getState().getPosition()
                .setX(position.getX())
                .setY(position.getY())
                .setAngle(angle);
    }

    private static Position getWheelPositionOnGround(double x, double wheelRadius, RoomModel roomModel) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(x - wheelRadius, x + wheelRadius, roomModel);
        var groundPosition = BattleUtils.getGroundPosition(groundIndexes.get(0), roomModel);
        var wheelPosition = new Position()
                .setX(x)
                .setY(getWheelYOnGround(x, wheelRadius, groundPosition));
        for (var i = 1; i < groundIndexes.size(); i++) {
            groundPosition = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            if (groundPosition.distanceTo(wheelPosition) < wheelRadius) {
                wheelPosition.setY(getWheelYOnGround(x, wheelRadius, groundPosition));
            }
        }
        return wheelPosition;
    }

    private static double getWheelYOnGround(double x, double wheelRadius, Position groundPosition) {
        var diff = Math.pow(wheelRadius, 2) - Math.pow(x - groundPosition.getX(), 2);
        return groundPosition.getY() + Math.signum(diff) * Math.sqrt(Math.abs(diff));
    }

    private static double getVehicleAngleOnGround(Position otherWheelPosition, short sign, double vehicleRadius,
                                                 double wheelRadius, RoomModel roomModel) {
        var xMin = sign > 0 ? otherWheelPosition.getX() : otherWheelPosition.getX() - 2 * vehicleRadius - wheelRadius;
        var xMax = sign > 0 ? otherWheelPosition.getX() + 2 * vehicleRadius + wheelRadius : otherWheelPosition.getX();
        var groundPositions = BattleUtils.getGroundIndexesBetween(xMin, xMax, roomModel).stream()
                .map(groundIndex -> BattleUtils.getGroundPosition(groundIndex, roomModel))
                .filter(groundPosition -> groundPosition.distanceTo(otherWheelPosition) > 2 * vehicleRadius - wheelRadius)
                .toList();
        var vehicleAngle = getVehicleAngleOnGround(otherWheelPosition, sign, groundPositions.get(0), vehicleRadius, wheelRadius);
        for (var i = 1; i < groundPositions.size(); i++) {
            var angle = getVehicleAngleOnGround(otherWheelPosition, sign, groundPositions.get(i), vehicleRadius, wheelRadius);
            if (sign > 0 && angle > vehicleAngle || sign <= 0 && angle < vehicleAngle) {
                vehicleAngle = angle;
            }
        }
        return vehicleAngle;
    }

    private static double getVehicleAngleOnGround(Position otherWheelPosition, short sign, Position groundPosition,
                                                 double vehicleRadius, double wheelRadius) {
        var xDiff = otherWheelPosition.getX() - groundPosition.getX();
        var yDiff = otherWheelPosition.getY() - groundPosition.getY();
        var sumCoefficient = sign * (Math.pow(wheelRadius, 2) - Math.pow(xDiff, 2) - Math.pow(yDiff, 2)
                - Math.pow(2 * vehicleRadius, 2)) / (4 * vehicleRadius);
        var multiplierCoefficient = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
        var ratio = sumCoefficient / multiplierCoefficient;
        return Math.asin(ratio > 1 ? 1 : ratio) - Math.asin(xDiff / multiplierCoefficient);
    }
}
