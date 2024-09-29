package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleUtils {

    public static Position getLeftWheelPosition(VehicleModel vehicleModel) {
        return getLeftWheelPosition(vehicleModel, vehicleModel.getState().getPosition(),
                vehicleModel.getState().getAngle());
    }

    public static Position getLeftWheelPosition(VehicleModel vehicleModel, Position position, double angle) {
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var wheelAngle = vehicleModel.getPreCalc().getWheelAngle();
        return new Position()
                .setX(position.getX() - wheelDistance * Math.cos(angle + wheelAngle))
                .setY(position.getY() - wheelDistance * Math.sin(angle + wheelAngle));
    }

    public static Position getRightWheelPosition(VehicleModel vehicleModel) {
        return getRightWheelPosition(vehicleModel, vehicleModel.getState().getPosition(),
                vehicleModel.getState().getAngle());
    }

    public static Position getRightWheelPosition(VehicleModel vehicleModel, Position position, double angle) {
        var wheelDistance = vehicleModel.getPreCalc().getWheelDistance();
        var wheelAngle = vehicleModel.getPreCalc().getWheelAngle();
        return new Position()
                .setX(position.getX() + wheelDistance * Math.cos(angle - wheelAngle))
                .setY(position.getY() + wheelDistance * Math.sin(angle - wheelAngle));
    }
}
