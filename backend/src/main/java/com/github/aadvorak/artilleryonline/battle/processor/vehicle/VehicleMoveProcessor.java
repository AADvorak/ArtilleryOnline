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
            recalculateVelocity(calculations, vehicleModel, battleModel);
        }
        calculateNextPositionAndAngle(calculations, vehicleModel, battleModel);
        if (!processCollisions(calculations, vehicleModel, battleModel)) {
            vehicleModel.getState().setPosition(calculations.getNextPosition());
            vehicleModel.getState().setAngle(calculations.getNextAngle());
        }
        vehicleModel.setCollided(false);
    }

    private static void recalculateVelocity(VehicleCalculations calculations, VehicleModel vehicleModel,BattleModel battleModel) {
        var acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(calculations, vehicleModel, battleModel.getRoom());
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
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
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        calculations.setNextPosition(new Position()
                .setX(position.getX() + vehicleVelocity.getX() * battleModel.getCurrentTimeStepSecs())
                .setY(position.getY() + vehicleVelocity.getY() * battleModel.getCurrentTimeStepSecs()));
        calculations.setNextAngle(angle + vehicleVelocity.getAngle() * battleModel.getCurrentTimeStepSecs());
    }
}
