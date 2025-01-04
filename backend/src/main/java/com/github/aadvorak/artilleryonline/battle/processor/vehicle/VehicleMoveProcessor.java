package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.elasticity.ElasticityAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;
import com.github.aadvorak.artilleryonline.battle.common.CollisionMode;
import com.github.aadvorak.artilleryonline.battle.calculator.VehicleAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.common.VehicleAcceleration;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleMoveProcessor {

    public static void processStep1(VehicleCalculations vehicle, BattleCalculations battle, String collisionMode) {
        recalculateAcceleration(vehicle, battle, collisionMode);
        VehicleUtils.calculateNextPositionAndAngle(vehicle, battle);
    }

    public static void processStep2(VehicleCalculations vehicle, BattleCalculations battle, String collisionMode) {
        if (CollisionMode.getCollisionMode(collisionMode).equals(CollisionMode.ELASTICITY) || !vehicle.isHasCollisions()) {
            applyNextPositionAndAngle(vehicle);
            recalculateVelocity(vehicle, battle);
        }
        calculateOnGround(vehicle);
    }

    private static void recalculateAcceleration(VehicleCalculations vehicle, BattleCalculations battle, String collisionMode) {
        var threshold = 0.3;
        var oldAcceleration = vehicle.getModel().getState().getAcceleration();
        var vehicleAcceleration = VehicleAccelerationCalculator.getVehicleAcceleration(vehicle, battle.getModel().getRoom());
        var elasticityAcceleration = CollisionMode.getCollisionMode(collisionMode).equals(CollisionMode.ELASTICITY)
                ? ElasticityAccelerationCalculator.getElasticityAcceleration(vehicle, battle)
                : new VehicleAcceleration();
        var acceleration = VehicleAcceleration.sumOf(vehicleAcceleration, elasticityAcceleration);
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

    private static void applyNextPositionAndAngle(VehicleCalculations vehicle) {
        vehicle.getModel().getState().setPosition(vehicle.getNextPosition());
        vehicle.getModel().getState().setAngle(vehicle.getNextAngle());
    }

    private static void calculateOnGround(VehicleCalculations vehicle) {
        var state = vehicle.getModel().getState();
        var onGround = !(vehicle.getLeftWheel().getGroundState().equals(WheelGroundState.FULL_OVER_GROUND)
                && vehicle.getRightWheel().getGroundState().equals(WheelGroundState.FULL_OVER_GROUND));
        if (state.isOnGround() != onGround) {
            state.setOnGround(onGround);
            vehicle.getModel().setUpdated(true);
        }
    }
}
