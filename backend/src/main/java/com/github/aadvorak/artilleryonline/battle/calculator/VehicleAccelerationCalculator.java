package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleAccelerationCalculator {

    public static BodyAcceleration getVehicleAcceleration(VehicleCalculations vehicle, RoomModel roomModel) {
        vehicle.recalculateWheelsVelocities();

        calculateWheelAcceleration(vehicle.getRightWheel(), vehicle.getModel(), roomModel);
        calculateWheelAcceleration(vehicle.getLeftWheel(), vehicle.getModel(), roomModel);

        var angleAcceleration = getVehicleAngleAcceleration(vehicle);
        var movingAcceleration = new Acceleration()
                .setX((vehicle.getRightWheel().getSumAcceleration().getX()
                        + vehicle.getLeftWheel().getSumAcceleration().getX()) / 2)
                .setY((vehicle.getRightWheel().getSumAcceleration().getY()
                        + vehicle.getLeftWheel().getSumAcceleration().getY()) / 2);

        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var frictionCoefficient = roomModel.getSpecs().getAirFrictionCoefficient();
        var frictionAcceleration = new Acceleration()
                .setX( - vehicleVelocity.getX() * Math.abs(vehicleVelocity.getX()) * frictionCoefficient)
                .setY( - vehicleVelocity.getY() * Math.abs(vehicleVelocity.getY()) * frictionCoefficient);

        return new BodyAcceleration()
                .setMovingAcceleration(Acceleration.sumOf(
                        movingAcceleration,
                        frictionAcceleration
                ))
                .setAngle(angleAcceleration - vehicleVelocity.getAngle());
    }

    private static double getVehicleAngleAcceleration(VehicleCalculations vehicle) {
        var angle = vehicle.getModel().getState().getPosition().getAngle();
        var rightWheelRotatingAcceleration = vehicle.getRightWheel().getSumAcceleration().getX() * Math.sin(angle)
                + vehicle.getRightWheel().getSumAcceleration().getY() * Math.cos(angle);
        var leftWheelRotatingAcceleration = vehicle.getLeftWheel().getSumAcceleration().getX() * Math.sin(angle)
                + vehicle.getLeftWheel().getSumAcceleration().getY() * Math.cos(angle);
        return ((rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2
                + getReturnOnWheelsRotatingAcceleration(angle)) / vehicle.getModel().getSpecs().getRadius();
    }

    private static double getReturnOnWheelsRotatingAcceleration(double angle) {
        if (angle >= Math.PI / 2 - 0.1) {
            return -2.0;
        }
        if (angle <= -Math.PI / 2 + 0.1) {
            return 2.0;
        }
        return 0.0;
    }

    private static void calculateWheelAcceleration(WheelCalculations wheelCalculations,
                                                   VehicleModel vehicleModel, RoomModel roomModel) {
        var roomGravityAcceleration = roomModel.getSpecs().getGravityAcceleration();
        var groundReactionCoefficient = roomModel.getSpecs().getGroundReactionCoefficient();
        var groundFrictionCoefficient = roomModel.getSpecs().getGroundFrictionCoefficient();
        var groundGravityDepth = 0.7 * roomModel.getSpecs().getGroundMaxDepth();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();

        GroundPositionCalculator.calculate(wheelCalculations, wheelRadius, roomModel);
        GroundStateCalculator.calculate(wheelCalculations);

        EngineAccelerationCalculator.calculate(wheelCalculations, vehicleModel);
        JetAccelerationCalculator.calculate(wheelCalculations, vehicleModel);
        GroundFrictionAccelerationCalculator.calculate(wheelCalculations, vehicleModel, groundFrictionCoefficient);
        GroundReactionAccelerationCalculator.calculate(wheelCalculations, vehicleModel, groundReactionCoefficient);
        GravityAccelerationCalculator.calculate(wheelCalculations, roomGravityAcceleration, groundGravityDepth);
    }
}
