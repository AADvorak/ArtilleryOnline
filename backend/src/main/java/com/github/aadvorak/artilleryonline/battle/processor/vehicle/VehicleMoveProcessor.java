package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.calculator.VehicleAccelerationCalculator;

public class VehicleMoveProcessor {

    public static void processStep1(VehicleCalculations vehicle, BattleCalculations battle) {
        recalculateAcceleration(vehicle, battle);
        recalculateVelocity(vehicle, battle);
        calculateNextPositionAndAngle(vehicle, battle);
    }

    public static void processStep2(VehicleCalculations vehicle, BattleCalculations battle) {
        if (!processCollisions(vehicle, battle)) {
            applyNextPositionAndAngle(vehicle);
        }
        vehicle.getModel().setCollided(false);
    }

    private static void recalculateAcceleration(VehicleCalculations vehicle, BattleCalculations battle) {
        var threshold = 0.3;
        var oldAcceleration = vehicle.getModel().getState().getAcceleration();
        var acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(vehicle, vehicle.getModel(),
                battle.getModel().getRoom());
        if (acceleration.getX() * oldAcceleration.getX() < 0
                && Math.abs(acceleration.getX() - oldAcceleration.getX()) > threshold) {
            vehicle.getModel().setUpdated(true);
        }
        vehicle.getModel().getState().setAcceleration(acceleration);
    }

    private static void recalculateVelocity(VehicleCalculations vehicle, BattleCalculations battle) {
        var acceleration = vehicle.getModel().getState().getAcceleration();
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var timeStep = battle.getModel().getCurrentTimeStepSecs();
        vehicleVelocity.setX(vehicleVelocity.getX() + acceleration.getX() * timeStep);
        vehicleVelocity.setY(vehicleVelocity.getY() + acceleration.getY() * timeStep);
        vehicleVelocity.setAngle(vehicleVelocity.getAngle() + acceleration.getAngle() * timeStep);
    }

    private static boolean processCollisions(VehicleCalculations vehicle, BattleCalculations battle) {
        if (VehicleWallCollideProcessor.processCollide(vehicle, battle)) {
            return true;
        }
        if (VehicleGroundCollideProcessor.processCollide(vehicle, battle)) {
            return true;
        }
        return VehicleCollideProcessor.processCollide(vehicle, battle);
    }

    private static void calculateNextPositionAndAngle(VehicleCalculations vehicle, BattleCalculations battle) {
        var position = vehicle.getModel().getState().getPosition();
        var angle = vehicle.getModel().getState().getAngle();
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var timeStep = battle.getModel().getCurrentTimeStepSecs();
        vehicle.setNextPosition(new Position()
                .setX(position.getX() + vehicleVelocity.getX() * timeStep)
                .setY(position.getY() + vehicleVelocity.getY() * timeStep));
        vehicle.setNextAngle(angle + vehicleVelocity.getAngle() * timeStep);
    }

    private static void applyNextPositionAndAngle(VehicleCalculations vehicle) {
        vehicle.getModel().getState().setPosition(vehicle.getNextPosition());
        var angle = vehicle.getNextAngle();
        if (angle > Math.PI / 2) {
            angle = Math.PI / 2;
        }
        if (angle < - Math.PI / 2) {
            angle = - Math.PI / 2;
        }
        vehicle.getModel().getState().setAngle(angle);
    }
}
