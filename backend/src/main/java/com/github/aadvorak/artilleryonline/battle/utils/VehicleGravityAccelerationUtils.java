package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

/**
 * todo improve and fix bugs
 */
public class VehicleGravityAccelerationUtils {

    public static double getVehicleGravityAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        return getLeftWheelAcceleration(vehicleModel, roomModel) + getRightWheelAcceleration(vehicleModel, roomModel);
    }

    private static double getRightWheelAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        var groundPositions = getWheelNearestGroundPositions(VehicleUtils.getRightWheelPosition(vehicleModel),
                vehicleModel.getSpecs().getWheelRadius(), roomModel);
        return getGravityAcceleration(groundPositions, roomModel.getSpecs().getGravityAcceleration(),
                vehicleModel.getState().getAngle());
    }

    private static double getLeftWheelAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        var groundPositions = getWheelNearestGroundPositions(VehicleUtils.getLeftWheelPosition(vehicleModel),
                vehicleModel.getSpecs().getWheelRadius(), roomModel);
        return getGravityAcceleration(groundPositions, roomModel.getSpecs().getGravityAcceleration(),
                vehicleModel.getState().getAngle());
    }

    private static double getGravityAcceleration(TwoPositions groundPositions, double gravityAcceleration, double vehicleAngle) {
        var groundAngle = Math.atan((groundPositions.second().getY() - groundPositions.first().getY())
                / (groundPositions.second().getX() - groundPositions.first().getX()));
        return - gravityAcceleration * Math.sin(groundAngle) * Math.cos(vehicleAngle - groundAngle); // todo debug
    }

    private static TwoPositions getWheelNearestGroundPositions(Position wheelPosition, double wheelRadius, RoomModel roomModel) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(wheelPosition.getX() - wheelRadius,
                wheelPosition.getX() + wheelRadius, roomModel);
        var i = groundIndexes.size() - 1;
        var groundPosition = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
        var groundDistance = groundPosition.distanceTo(wheelPosition);
        var groundIndex = groundIndexes.get(i);
        if (groundDistance <= wheelRadius) {
            return getTwoGroundPositions(groundPosition, groundIndex, roomModel);
        }
        for (i = groundIndexes.size() - 2; i >= 0; i--) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(wheelPosition);
            if (distance <= wheelRadius) {
                return getTwoGroundPositions(position, groundIndexes.get(i), roomModel);
            }
            if (distance <= groundDistance) {
                groundPosition = position;
                groundDistance = distance;
                groundIndex = groundIndexes.get(i);
            }
        }
        return getTwoGroundPositions(groundPosition, groundIndex, roomModel);
    }

    private static TwoPositions getTwoGroundPositions(Position position, int index, RoomModel roomModel) {
        if (index >= 1) {
            return new TwoPositions(BattleUtils.getGroundPosition(index - 1, roomModel), position);
        } else {
            return new TwoPositions(position, BattleUtils.getGroundPosition(index + 1, roomModel));
        }
    }

    private record TwoPositions(Position first, Position second) {
    }
}
