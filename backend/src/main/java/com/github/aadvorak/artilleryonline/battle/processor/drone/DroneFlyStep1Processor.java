package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneTargetCalculator;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.Step1Processor;
import org.springframework.stereotype.Component;

@Component
public class DroneFlyStep1Processor implements Step1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getDrones().forEach(drone -> processDrone(drone, battle.getModel()));
    }

    private void processDrone(DroneCalculations drone, BattleModel battleModel) {
        DroneTargetCalculator.calculate(drone, battleModel);
        recalculateVelocity(drone, battleModel);
        drone.calculateNextPosition(battleModel.getCurrentTimeStepSecs());
    }

    private void recalculateVelocity(DroneCalculations drone, BattleModel battleModel) {
        var acceleration = DroneAccelerationCalculator.calculate(drone, battleModel);
        var velocity = drone.getModel().getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }
}
