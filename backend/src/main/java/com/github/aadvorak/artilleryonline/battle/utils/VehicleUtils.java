package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleUtils {

    public static Position getNextLeftWheelPosition(VehicleCalculations vehicle) {
        return getLeftWheelPosition(vehicle.getModel(), vehicle.getNextPosition(), vehicle.getNextAngle());
    }

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

    public static Position getNextRightWheelPosition(VehicleCalculations vehicle) {
        return getRightWheelPosition(vehicle.getModel(), vehicle.getNextPosition(), vehicle.getNextAngle());
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

    public static void recalculateVehicleVelocityByWheel(VehicleCalculations calculations, WheelCalculations wheelCalculations) {
        var rightWheelVelocity = calculations.getRightWheel().getVelocity();
        var leftWheelVelocity = calculations.getLeftWheel().getVelocity();
        var angle = calculations.getModel().getState().getAngle();

        var angleVelocity = Math.abs(angle) < Math.PI / 4
                ? (rightWheelVelocity.getY() - leftWheelVelocity.getY()) / (2.0 * Math.cos(angle))
                : (leftWheelVelocity.getX() - rightWheelVelocity.getX()) / (2.0 * Math.sin(angle));
        var wheelSign = wheelCalculations.getSign().getValue();
        calculations.getModel().getState().getVelocity()
                .setAngle(angleVelocity)
                .setX(wheelCalculations.getVelocity().getX() - wheelSign * angleVelocity * Math.sin(angle))
                .setY(wheelCalculations.getVelocity().getY() + wheelSign * angleVelocity * Math.cos(angle));
    }

    public static void calculateNextPositionAndAngle(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.setNextPosition(vehicle.getModel().getState().getPosition());
        vehicle.setNextAngle(vehicle.getModel().getState().getAngle());
        recalculateNextPositionAndAngle(vehicle, battle);
    }

    public static void recalculateNextPositionAndAngle(VehicleCalculations vehicle, BattleCalculations battle) {
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var timeStep = battle.getModel().getCurrentTimeStepSecs();
        var positionShift = new Position()
                .setX(vehicleVelocity.getX() * timeStep)
                .setY(vehicleVelocity.getY() * timeStep);
        addToNextPositionAndAngle(vehicle, positionShift, vehicleVelocity.getAngle() * timeStep);
    }

    private static void addToNextPositionAndAngle(VehicleCalculations vehicle, Position positionShift, double angleShift) {
        var position = vehicle.getNextPosition();
        position
                .setX(position.getX() + positionShift.getX())
                .setY(position.getY() + positionShift.getY());
        var nextAngle = vehicle.getNextAngle() + angleShift;
        if (nextAngle > Math.PI / 2) {
            nextAngle = Math.PI / 2;
        }
        if (nextAngle < -Math.PI / 2) {
            nextAngle = -Math.PI / 2;
        }
        vehicle.setNextAngle(nextAngle);
    }
}
