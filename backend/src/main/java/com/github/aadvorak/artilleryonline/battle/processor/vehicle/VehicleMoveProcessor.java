package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.calculator.VehicleAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var calculations = new VehicleCalculations();
        if (!vehicleModel.isCollided()) {
            recalculateVelocity(calculations, vehicleModel, battleModel);
        }
        calculateNextPositionAndAngle(calculations, vehicleModel, battleModel);
        if (wallCollide(calculations, vehicleModel, battleModel)) {
            vehicleModel.getState().getVehicleVelocity().setX(
                    - vehicleModel.getState().getVehicleVelocity().getX() / 2);
            battleModel.setUpdated(true);
            return;
        }
        if (VehicleCollideProcessor.processCollide(calculations, vehicleModel, battleModel)) {
            return;
        }
        if (VehicleGroundCollideProcessor.processCollide(calculations, vehicleModel, battleModel)) {
            return;
        }
        vehicleModel.getState().setPosition(calculations.getNextPosition());
        vehicleModel.getState().setAngle(calculations.getNextAngle());
        vehicleModel.setCollided(false);
    }

    private static void recalculateVelocity(VehicleCalculations calculations, VehicleModel vehicleModel, BattleModel battleModel) {
        var acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(calculations, vehicleModel, battleModel.getRoom());
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        vehicleVelocity.setX(vehicleVelocity.getX() + acceleration.getX() * battleModel.getCurrentTimeStepSecs());
        vehicleVelocity.setY(vehicleVelocity.getY() + acceleration.getY() * battleModel.getCurrentTimeStepSecs());
        vehicleVelocity.setAngle(vehicleVelocity.getAngle() + acceleration.getAngle() * battleModel.getCurrentTimeStepSecs());
    }

    private static boolean wallCollide(VehicleCalculations calculations, VehicleModel vehicleModel, BattleModel battleModel) {
        var velocityX = vehicleModel.getState().getVehicleVelocity().getX();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        if (velocityX > 0) {
            var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, calculations.getNextPosition(),
                    calculations.getNextAngle());
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (velocityX < 0) {
            var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, calculations.getNextPosition(),
                    calculations.getNextAngle());
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
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
