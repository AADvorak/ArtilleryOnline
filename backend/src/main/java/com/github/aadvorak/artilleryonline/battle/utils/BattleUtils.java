package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.LeftAndRightPositions;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

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

    public static double getRoomWidth(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getX() - roomSpecs.getLeftBottom().getX();
    }

    public static double getRoomHeight(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getY() - roomSpecs.getLeftBottom().getY();
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
