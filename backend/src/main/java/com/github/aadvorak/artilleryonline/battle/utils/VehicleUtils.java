package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleUtils {

    public static Position getLeftWheelPosition(VehicleModel vehicleModel) {
        return getLeftWheelPosition(vehicleModel, vehicleModel.getState().getPosition());
    }

    public static Position getLeftWheelPosition(VehicleModel vehicleModel, Position position) {
        var angle = vehicleModel.getState().getAngle();
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var wheelAngle = vehicleModel.getPreCalc().getWheelAngle();
        return new Position()
                .setX(position.getX() - wheelDistance * Math.cos(angle + wheelAngle))
                .setY(position.getY() - wheelDistance * Math.sin(angle + wheelAngle));
    }

    public static Position getRightWheelPosition(VehicleModel vehicleModel) {
        return getRightWheelPosition(vehicleModel, vehicleModel.getState().getPosition());
    }

    public static Position getRightWheelPosition(VehicleModel vehicleModel, Position position) {
        var angle = vehicleModel.getState().getAngle();
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var wheelAngle = vehicleModel.getPreCalc().getWheelAngle();
        return new Position()
                .setX(position.getX() + wheelDistance * Math.cos(angle - wheelAngle))
                .setY(position.getY() + wheelDistance * Math.sin(angle - wheelAngle));
    }

    public static double getVehicleAcceleration(VehicleModel vehicleModel) {
        return getEngineAcceleration(vehicleModel) + getFrictionAcceleration(vehicleModel);
    }

    private static double getEngineAcceleration(VehicleModel vehicleModel) {
        if (vehicleModel.getState().getTrackState().isBroken()) {
            return 0;
        }
        var direction = vehicleModel.getState().getMovingDirection();
        var acceleration = vehicleModel.getSpecs().getAcceleration();
        if (MovingDirection.RIGHT.equals(direction)) {
            return acceleration;
        }
        if (MovingDirection.LEFT.equals(direction)) {
            return -acceleration;
        }
        return 0.0;
    }

    private static double getFrictionAcceleration(VehicleModel vehicleModel) {
        var velocity = vehicleModel.getState().getVelocity();
        return - vehicleModel.getPreCalc().getFrictionCoefficient() * velocity * Math.abs(velocity);
    }

    public static double getGravityAcceleration(VehicleModel vehicleModel, double gravityAcceleration) {
        return 0.5 * gravityAcceleration * Math.cos(Math.PI / 2 + vehicleModel.getState().getAngle());
    }
}
