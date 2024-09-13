package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.LeftAndRightPositions;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BattleUtils {

    public static void correctVehiclePositionAndAngleOnGround(VehicleState vehicleState, RoomModel roomModel) {
        var groundPositions = getLeftAndRightGroundPositions(vehicleState.getPosition().getX(), roomModel);
        var minY = Math.min(groundPositions.getLeft().getY(), groundPositions.getRight().getY());
        var maxY = Math.max(groundPositions.getLeft().getY(), groundPositions.getRight().getY());
        var y = vehicleState.getPosition().getY();
        if (y < minY) {
            y = minY;
        } else if (y > maxY) {
            y = maxY;
        }
        vehicleState.getPosition().setY(y);
        vehicleState.setAngle(Math.atan((groundPositions.getRight().getY() - groundPositions.getLeft().getY())
                / (groundPositions.getRight().getX() - groundPositions.getLeft().getX())));
    }

    public static void correctVehiclePositionAndAngleOnGround(VehicleModel vehicleModel, RoomModel roomModel) {
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var angle = vehicleModel.getState().getAngle();
        var wheelDistance = Math.sqrt(Math.pow(wheelRadius, 2) + Math.pow(vehicleRadius, 2));
        var wheelAngle = Math.atan(wheelRadius / vehicleRadius);
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

    public static Position getLeftWheelPosition(VehicleModel vehicleModel, Position position) {
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var angle = vehicleModel.getState().getAngle();
        var wheelDistance = Math.sqrt(Math.pow(wheelRadius, 2) + Math.pow(vehicleRadius, 2));
        var wheelAngle = Math.atan(wheelRadius / vehicleRadius);
        return new Position()
                .setX(position.getX() - wheelDistance * Math.cos(angle + wheelAngle))
                .setY(position.getY() - wheelDistance * Math.sin(angle + wheelAngle));
    }

    public static Position getRightWheelPosition(VehicleModel vehicleModel, Position position) {
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var angle = vehicleModel.getState().getAngle();
        var wheelDistance = Math.sqrt(Math.pow(wheelRadius, 2) + Math.pow(vehicleRadius, 2));
        var wheelAngle = Math.atan(wheelRadius / vehicleRadius);
        return new Position()
                .setX(position.getX() + wheelDistance * Math.cos(angle - wheelAngle))
                .setY(position.getY() + wheelDistance * Math.sin(angle - wheelAngle));
    }

    public static double getRoomWidth(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getX() - roomSpecs.getLeftBottom().getX();
    }

    public static double getRoomHeight(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getY() - roomSpecs.getLeftBottom().getY();
    }

    public static Position getLeftWheelPositionOnGround(double x, double wheelRadius, RoomModel roomModel) {
        var groundIndexes = getGroundIndexesBetween(x - wheelRadius, x + wheelRadius, roomModel);
        var groundPosition = getGroundPosition(groundIndexes.get(0), roomModel);
        var wheelPosition = new Position()
                .setX(x)
                .setY(getLeftWheelY(x, wheelRadius, groundPosition));
        for (var i = 1; i < groundIndexes.size(); i++) {
            groundPosition = getGroundPosition(groundIndexes.get(i), roomModel);
            if (groundPosition.distanceTo(wheelPosition) < wheelRadius) {
                wheelPosition.setY(getLeftWheelY(x, wheelRadius, groundPosition));
            }
        }
        return wheelPosition;
    }

    public static double getLeftWheelY(double x, double wheelRadius, Position groundPosition) {
        return groundPosition.getY() + Math.sqrt(Math.pow(wheelRadius, 2) - Math.pow(x - groundPosition.getX(), 2));
    }

    public static double getVehicleAngleOnGround(Position leftWheelPosition, double vehicleRadius,
                                                 double wheelRadius, RoomModel roomModel) {
        var groundPositions = getGroundIndexesBetween(leftWheelPosition.getX(),
                leftWheelPosition.getX() + 2 * vehicleRadius + wheelRadius, roomModel).stream()
                .map(groundIndex -> getGroundPosition(groundIndex, roomModel))
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

    public static double getVehicleAngleOnGround(Position rightWheelPosition, Position groundPosition,
                                                 double vehicleRadius, double wheelRadius) {
        var xDiff = rightWheelPosition.getX() - groundPosition.getX();
        var yDiff = rightWheelPosition.getY() - groundPosition.getY();
        var sumCoefficient = (Math.pow(wheelRadius, 2) - Math.pow(xDiff, 2) - Math.pow(yDiff, 2)
                - Math.pow(2 * vehicleRadius, 2)) / (4 * vehicleRadius);
        var multiplierCoefficient = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
        return Math.asin(sumCoefficient / multiplierCoefficient) - Math.asin(xDiff / multiplierCoefficient);
    }

    public static List<Integer> getGroundIndexesBetween(double xMin, double xMax,  RoomModel roomModel) {
        var roomWidth = getRoomWidth(roomModel.getSpecs());
        var groundPointsNumber = roomModel.getState().getGroundLine().size();
        var minGroundIndex = (int) Math.ceil((double) groundPointsNumber * xMin / roomWidth);
        var maxGroundIndex = (int) Math.floor((double) groundPointsNumber * xMax / roomWidth);
        if (minGroundIndex < 0) {
            minGroundIndex = 0;
        }
        if (maxGroundIndex >= groundPointsNumber) {
            maxGroundIndex = groundPointsNumber - 1;
        }
        return IntStream.range(minGroundIndex, maxGroundIndex)
                .boxed()
                .collect(Collectors.toList());
    }

    public static Position getNearestGroundPosition(double x, RoomModel roomModel) {
        var roomWidth = getRoomWidth(roomModel.getSpecs());
        var groundPointsNumber = roomModel.getState().getGroundLine().size();
        var objectPositionIndex = (double) groundPointsNumber * x / roomWidth;
        var nearestGroundIndex = (int) Math.floor(objectPositionIndex);
        if (nearestGroundIndex < 0) {
            nearestGroundIndex = 0;
        }
        if (nearestGroundIndex >= groundPointsNumber) {
            nearestGroundIndex = groundPointsNumber - 1;
        }
        return getGroundPosition(nearestGroundIndex, roomModel);
    }

    public static LeftAndRightPositions getLeftAndRightGroundPositions(double x, RoomModel roomModel) {
        var roomWidth = getRoomWidth(roomModel.getSpecs());
        var groundPointsNumber = roomModel.getState().getGroundLine().size();
        var objectPositionIndex = (double) groundPointsNumber * x / roomWidth;
        var leftGroundIndex = (int) Math.floor(objectPositionIndex);
        var rightGroundIndex = (int) Math.ceil(objectPositionIndex);
        if (leftGroundIndex == rightGroundIndex) {
            rightGroundIndex++;
        }
        if (leftGroundIndex < 0) {
            leftGroundIndex = 0;
            rightGroundIndex = 1;
        }
        if (rightGroundIndex >= groundPointsNumber) {
            rightGroundIndex = groundPointsNumber - 1;
            leftGroundIndex = groundPointsNumber - 2;
        }
        return new LeftAndRightPositions()
                .setLeft(getGroundPosition(leftGroundIndex, roomModel))
                .setRight(getGroundPosition(rightGroundIndex, roomModel));
    }

    public static Position getGroundPosition(int index, RoomModel roomModel) {
        return new Position()
                .setX((double) index * roomModel.getSpecs().getStep())
                .setY(roomModel.getState().getGroundLine().get(index));
    }

    public static double generateRandom(double min, double max) {
        return min + (Math.random() * (max - min));
    }
}
