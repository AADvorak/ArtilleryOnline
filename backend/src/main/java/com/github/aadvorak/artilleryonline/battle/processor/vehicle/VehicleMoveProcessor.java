package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.calculator.VehicleAccelerationCalculator;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var calculations = new VehicleCalculations();
        if (!vehicleModel.isCollided()) {
            recalculateAcceleration(calculations, vehicleModel, battleModel);
            recalculateVelocity(vehicleModel, battleModel);
        }
        calculateNextPositionAndAngle(calculations, vehicleModel, battleModel);
        if (!processCollisions(calculations, vehicleModel, battleModel)) {
            applyNextPositionAndAngle(calculations, vehicleModel);
        }
        vehicleModel.setCollided(false);
    }

    private static void recalculateAcceleration(VehicleCalculations calculations, VehicleModel vehicleModel, BattleModel battleModel) {
        var threshold = 0.3;
        var oldAcceleration = vehicleModel.getState().getAcceleration();
        var acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(calculations, vehicleModel, battleModel.getRoom());
        if (acceleration.getX() * oldAcceleration.getX() < 0
                && Math.abs(acceleration.getX() - oldAcceleration.getX()) > threshold) {
            battleModel.setUpdated(true);
        }
        vehicleModel.getState().setAcceleration(acceleration);
    }

    private static void recalculateVelocity(VehicleModel vehicleModel, BattleModel battleModel) {
        var acceleration = vehicleModel.getState().getAcceleration();
        var vehicleVelocity = vehicleModel.getState().getVelocity();
        vehicleVelocity.setX(vehicleVelocity.getX() + acceleration.getX() * battleModel.getCurrentTimeStepSecs());
        vehicleVelocity.setY(vehicleVelocity.getY() + acceleration.getY() * battleModel.getCurrentTimeStepSecs());
        vehicleVelocity.setAngle(vehicleVelocity.getAngle() + acceleration.getAngle() * battleModel.getCurrentTimeStepSecs());
    }

    private static boolean processCollisions(VehicleCalculations calculations, VehicleModel vehicleModel, BattleModel battleModel) {
        if (VehicleWallCollideProcessor.processCollide(calculations, vehicleModel, battleModel)) {
            return true;
        }
        if (VehicleCollideProcessor.processCollide(calculations, vehicleModel, battleModel)) {
            return true;
        }
        return VehicleGroundCollideProcessor.processCollide(calculations, vehicleModel, battleModel);
    }

    private static void calculateNextPositionAndAngle(VehicleCalculations calculations, VehicleModel vehicleModel,
                                                      BattleModel battleModel) {
        var position = vehicleModel.getState().getPosition();
        var angle = vehicleModel.getState().getAngle();
        var vehicleVelocity = vehicleModel.getState().getVelocity();
        calculations.setNextPosition(new Position()
                .setX(position.getX() + vehicleVelocity.getX() * battleModel.getCurrentTimeStepSecs())
                .setY(position.getY() + vehicleVelocity.getY() * battleModel.getCurrentTimeStepSecs()));
        calculations.setNextAngle(angle + vehicleVelocity.getAngle() * battleModel.getCurrentTimeStepSecs());
    }

    private static void applyNextPositionAndAngle(VehicleCalculations calculations, VehicleModel vehicleModel) {
        vehicleModel.getState().setPosition(calculations.getNextPosition());
        var angle = calculations.getNextAngle();
        if (angle > Math.PI / 2) {
            angle = Math.PI / 2;
        }
        if (angle < - Math.PI / 2) {
            angle = - Math.PI / 2;
        }
        vehicleModel.getState().setAngle(angle);
    }
}
