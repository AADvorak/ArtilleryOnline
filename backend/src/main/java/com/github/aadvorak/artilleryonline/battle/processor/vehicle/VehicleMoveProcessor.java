package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.VehicleAccelerationCalculator;

public class VehicleMoveProcessor {

    public static void processStep1(VehicleCalculations vehicle, BattleCalculations battle) {
        recalculateAcceleration(vehicle, battle);
        recalculateVelocity(vehicle, battle);
        vehicle.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        vehicle.recalculateWheelsVelocities();
    }

    public static void processStep2(VehicleCalculations vehicle) {
        correctTargetAngle(vehicle);
        vehicle.applyNextPosition();
        calculateOnGround(vehicle);
    }

    private static void recalculateAcceleration(VehicleCalculations vehicle, BattleCalculations battle) {
        var threshold = 0.3;
        var oldAcceleration = vehicle.getModel().getState().getAcceleration();
        var acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(vehicle, battle.getModel());;
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
        vehicleVelocity.recalculate(acceleration, timeStep);
    }

    private static void calculateOnGround(VehicleCalculations vehicle) {
        var state = vehicle.getModel().getState();
        var onGround = vehicle.getRightWheel().getGroundContact() != null
                || vehicle.getLeftWheel().getGroundContact() != null;
        if (state.isOnGround() != onGround) {
            state.setOnGround(onGround);
            vehicle.getModel().setUpdated(true);
        }
    }

    private static void correctTargetAngle(VehicleCalculations vehicle) {
        var angleDiff = vehicle.getNextPosition().getAngle() - vehicle.getModel().getState().getPosition().getAngle();
        if (angleDiff * vehicle.getModel().getState().getVelocity().getAngle() < 0) {
            var gunState = vehicle.getModel().getState().getGunState();
            var targetAngle = gunState.getTargetAngle();
            if (angleDiff > 0) {
                gunState.setTargetAngle(targetAngle + Math.PI * 2);
            }
            if (angleDiff < 0) {
                gunState.setTargetAngle(targetAngle - Math.PI * 2);
            }
        }
    }
}
