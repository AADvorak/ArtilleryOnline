package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
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

    public static void calculateWheelVelocity(VehicleModel vehicleModel, WheelCalculations wheelCalculations) {
        var vehicleVelocity = vehicleModel.getState().getVelocity();
        var angle = vehicleModel.getState().getAngle();
        var angleVelocity = vehicleVelocity.getAngle() * vehicleModel.getSpecs().getRadius();
        var velocityX = vehicleVelocity.getX() + wheelCalculations.getSign().getValue() * angleVelocity * Math.sin(angle);
        var velocityY = vehicleVelocity.getY() - wheelCalculations.getSign().getValue() * angleVelocity * Math.cos(angle);
        wheelCalculations.setVelocity(new Velocity()
                .setX(velocityX)
                .setY(velocityY));
    }

    public static void recalculateVehicleVelocityByWheel(VehicleModel vehicle, VehicleCalculations calculations,
                                                         WheelCalculations wheelCalculations) {
        var rightWheelVelocity = calculations.getRightWheel().getVelocity();
        var leftWheelVelocity = calculations.getLeftWheel().getVelocity();
        var angle = vehicle.getState().getAngle();

        var angleVelocity = Math.abs(angle) < Math.PI / 4
                ? (rightWheelVelocity.getY() - leftWheelVelocity.getY()) / (2.0 * Math.cos(angle))
                : (leftWheelVelocity.getX() - rightWheelVelocity.getX()) / (2.0 * Math.sin(angle));
        var wheelSign = wheelCalculations.getSign().getValue();
        vehicle.getState().getVelocity()
                .setAngle(angleVelocity)
                .setX(wheelCalculations.getVelocity().getX() - wheelSign * angleVelocity * Math.sin(angle))
                .setY(wheelCalculations.getVelocity().getY() + wheelSign * angleVelocity * Math.cos(angle));
    }
}
