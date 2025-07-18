package com.github.aadvorak.artilleryonline.battle.processor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.Step1Processor;
import org.springframework.stereotype.Component;

@Component
public class MissileFlyStep1Processor implements Step1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getMissiles().forEach(missile ->
                missile.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs()));
    }
}
