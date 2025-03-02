package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneTargetCalculator;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class DroneFlyProcessor {

    public static void processStep1(DroneCalculations drone, BattleModel battleModel) {
        DroneTargetCalculator.calculate(drone, battleModel);
        recalculateVelocity(drone, battleModel);
        drone.calculateNextPosition(battleModel.getCurrentTimeStepSecs());
    }

    public static void processStep2(DroneCalculations drone, BattleModel battleModel) {
        drone.applyNextPosition();
        if (drone.getPosition().getY() > 1.5 * BattleUtils.getRoomHeight(battleModel.getRoom().getSpecs())) {
            battleModel.getUpdates().removeDrone(drone.getId());
        }
    }

    private static void recalculateVelocity(DroneCalculations drone, BattleModel battleModel) {
        var acceleration = DroneAccelerationCalculator.calculate(drone, battleModel);
        var velocity = drone.getModel().getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }
}
