package com.github.aadvorak.artilleryonline.battle.calculator.elasticity;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.VehicleAcceleration;

public class ElasticityAccelerationCalculator {

    public static VehicleAcceleration getElasticityAcceleration(VehicleCalculations vehicle, BattleCalculations battle) {
        var angle = vehicle.getModel().getState().getAngle();

        VehicleElasticityAccelerationCalculator.calculate(vehicle, battle);
        GroundElasticityAccelerationCalculator.calculate(vehicle, battle);
        WallElasticityAccelerationCalculator.calculate(vehicle, battle);

        var wheelsAcceleration = new Acceleration()
                .setX((vehicle.getRightWheel().getSumElasticityAcceleration().getX()
                        + vehicle.getLeftWheel().getSumElasticityAcceleration().getX()) / 2)
                .setY((vehicle.getRightWheel().getSumElasticityAcceleration().getY()
                        + vehicle.getLeftWheel().getSumElasticityAcceleration().getY()) / 2);
        var rotatingAcceleration = getVehicleRotatingAcceleration(vehicle, angle);

        return new VehicleAcceleration()
                .setMovingAcceleration(Acceleration.sumOf(
                        wheelsAcceleration,
                        vehicle.getSumElasticityAcceleration()
                ))
                .setAngle(rotatingAcceleration /  vehicle.getModel().getSpecs().getRadius());
    }

    private static double getVehicleRotatingAcceleration(VehicleCalculations calculations, double angle) {
        var rightWheelRotatingAcceleration = calculations.getRightWheel().getSumElasticityAcceleration().getX() * Math.sin(angle)
                + calculations.getRightWheel().getSumElasticityAcceleration().getY() * Math.cos(angle);
        var leftWheelRotatingAcceleration = calculations.getLeftWheel().getSumElasticityAcceleration().getX() * Math.sin(angle)
                + calculations.getLeftWheel().getSumElasticityAcceleration().getY() * Math.cos(angle);
        return (rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2;
    }
}
