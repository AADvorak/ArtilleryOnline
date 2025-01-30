package com.github.aadvorak.artilleryonline.battle.processor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.MissileAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class MissileFlyProcessor {

    public static void processStep1(MissileCalculations missile, BattleModel battleModel) {
        missile.calculateNextPosition(battleModel.getCurrentTimeStepSecs());
    }

    public static void processStep2(MissileCalculations missile, BattleModel battleModel) {
        missile.applyNextPosition();
        recalculateVelocity(missile, battleModel);
    }

    private static void recalculateVelocity(MissileCalculations missile, BattleModel battleModel) {
        var acceleration = MissileAccelerationCalculator.calculate(missile, battleModel);
        var velocity = missile.getModel().getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }
}
