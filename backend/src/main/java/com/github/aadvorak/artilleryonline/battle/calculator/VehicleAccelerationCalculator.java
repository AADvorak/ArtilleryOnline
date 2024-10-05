package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleAccelerationCalculator {

    public static VehicleAcceleration getVehicleAcceleration(VehicleCalculations calculations,
                                                             VehicleModel vehicleModel, RoomModel roomModel) {
        var angle = vehicleModel.getState().getAngle();

        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getLeftWheel());

        calculations.getRightWheel().setPosition(VehicleUtils.getRightWheelPosition(vehicleModel));
        calculations.getLeftWheel().setPosition(VehicleUtils.getLeftWheelPosition(vehicleModel));

        calculateWheelAcceleration(calculations.getRightWheel(), vehicleModel, roomModel);
        calculateWheelAcceleration(calculations.getLeftWheel(), vehicleModel, roomModel);

        var rotatingAcceleration = getVehicleRotatingAcceleration(calculations, angle);
        var movingAcceleration = new Acceleration()
                .setX((calculations.getRightWheel().getSumAcceleration().getX()
                        + calculations.getLeftWheel().getSumAcceleration().getX()) / 2)
                .setY((calculations.getRightWheel().getSumAcceleration().getY()
                        + calculations.getLeftWheel().getSumAcceleration().getY()) / 2);

        var vehicleVelocity = vehicleModel.getState().getVelocity();
        var frictionCoefficient = vehicleModel.getPreCalc().getFrictionCoefficient();
        var frictionAcceleration = new Acceleration()
                .setX( - vehicleVelocity.getX() * Math.abs(vehicleVelocity.getX()) * frictionCoefficient)
                .setY( - vehicleVelocity.getY() * Math.abs(vehicleVelocity.getY()) * frictionCoefficient);

        return new VehicleAcceleration()
                .setMovingAcceleration(Acceleration.sumOf(
                        movingAcceleration,
                        frictionAcceleration
                ))
                .setAngle(rotatingAcceleration / vehicleModel.getSpecs().getRadius()
                        - vehicleVelocity.getAngle());
    }

    private static double getVehicleRotatingAcceleration(VehicleCalculations calculations, double angle) {
        var rightWheelRotatingAcceleration = calculations.getRightWheel().getSumAcceleration().getX() * Math.sin(angle)
                + calculations.getRightWheel().getSumAcceleration().getY() * Math.cos(angle);
        var leftWheelRotatingAcceleration = calculations.getLeftWheel().getSumAcceleration().getX() * Math.sin(angle)
                + calculations.getLeftWheel().getSumAcceleration().getY() * Math.cos(angle);
        return (rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2;
    }

    private static void calculateWheelAcceleration(WheelCalculations wheelCalculations,
                                                   VehicleModel vehicleModel, RoomModel roomModel) {
        var roomGravityAcceleration = roomModel.getSpecs().getGravityAcceleration();
        var groundReactionCoefficient = roomModel.getSpecs().getGroundReactionCoefficient();
        var groundFrictionCoefficient = roomModel.getSpecs().getGroundFrictionCoefficient();
        var groundMaxDepth = roomModel.getSpecs().getGroundMaxDepth();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();

        GroundPositionCalculator.calculate(wheelCalculations, wheelRadius, roomModel);
        GroundStateCalculator.calculate(wheelCalculations);

        EngineAccelerationCalculator.calculate(wheelCalculations, vehicleModel);
        GroundFrictionAccelerationCalculator.calculate(wheelCalculations, vehicleModel, groundFrictionCoefficient);
        GroundReactionAccelerationCalculator.calculate(wheelCalculations, groundReactionCoefficient);
        GravityAccelerationCalculator.calculate(wheelCalculations, roomGravityAcceleration, groundMaxDepth);
    }
}
