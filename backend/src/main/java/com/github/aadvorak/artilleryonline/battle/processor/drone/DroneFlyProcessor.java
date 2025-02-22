package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneTargetCalculator;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class DroneFlyProcessor {

    public static void processStep1(DroneCalculations drone, BattleModel battleModel) {
        DroneTargetCalculator.calculate(drone, battleModel);
        recalculateVelocity(drone, battleModel);
        drone.calculateNextPosition(battleModel.getCurrentTimeStepSecs());
    }

    public static void processStep2(DroneCalculations drone, BattleModel battleModel) {
        drone.applyNextPosition();
    }

    private static void recalculateVelocity(DroneCalculations drone, BattleModel battleModel) {
        var acceleration = DroneAccelerationCalculator.calculate(drone, battleModel);
        var velocity = drone.getModel().getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }
}
