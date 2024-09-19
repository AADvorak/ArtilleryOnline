package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleGravityAccelerationUtils {

    public static double getVehicleGravityAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        return getLeftWheelAcceleration(vehicleModel, roomModel) + getRightWheelAcceleration(vehicleModel, roomModel);
    }

    private static double getRightWheelAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        var wheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel);
        var groundPosition = getWheelNearestGroundPositions(wheelPosition,
                vehicleModel.getSpecs().getWheelRadius(), roomModel);
        return getGravityAcceleration(wheelPosition, groundPosition, roomModel.getSpecs().getGravityAcceleration(),
                vehicleModel.getState().getAngle());
    }

    private static double getLeftWheelAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        var wheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel);
        var groundPosition = getWheelNearestGroundPositions(wheelPosition,
                vehicleModel.getSpecs().getWheelRadius(), roomModel);
        return getGravityAcceleration(wheelPosition, groundPosition, roomModel.getSpecs().getGravityAcceleration(),
                vehicleModel.getState().getAngle());
    }

    private static double getGravityAcceleration(Position wheelPosition, Position groundPosition,
                                                 double gravityAcceleration, double vehicleAngle) {
        var groundAngle = Math.atan((groundPosition.getX() - wheelPosition.getX())
                / Math.abs(wheelPosition.getY() - groundPosition.getY()));
        return - 0.5 * gravityAcceleration * Math.sin(groundAngle) * Math.cos(vehicleAngle - groundAngle);
    }

    private static Position getWheelNearestGroundPositions(Position wheelPosition, double wheelRadius, RoomModel roomModel) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(wheelPosition.getX() - wheelRadius,
                wheelPosition.getX() + wheelRadius, roomModel);
        var i = groundIndexes.size() - 1;
        var groundPosition = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
        var groundDistance = groundPosition.distanceTo(wheelPosition);
        if (groundDistance <= wheelRadius) {
            return groundPosition;
        }
        for (i = groundIndexes.size() - 2; i >= 0; i--) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(wheelPosition);
            if (distance <= wheelRadius) {
                return position;
            }
            if (distance <= groundDistance) {
                groundPosition = position;
                groundDistance = distance;
            }
        }
        return groundPosition;
    }
}
