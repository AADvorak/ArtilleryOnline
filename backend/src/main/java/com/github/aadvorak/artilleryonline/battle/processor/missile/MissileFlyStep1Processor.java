package com.github.aadvorak.artilleryonline.battle.processor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.MissileAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.Step1Processor;
import org.springframework.stereotype.Component;

@Component
public class MissileFlyStep1Processor implements Step1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getMissiles().forEach(missile -> processMissile(missile, battle));
    }

    private void processMissile(MissileCalculations missile, BattleCalculations battle) {
        recalculateVelocity(missile, battle.getModel());
        missile.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
    }

    private void recalculateVelocity(MissileCalculations missile, BattleModel battleModel) {
        var acceleration = MissileAccelerationCalculator.calculate(missile, battleModel);
        var velocity = missile.getModel().getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }
}
