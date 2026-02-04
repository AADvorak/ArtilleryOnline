package com.github.aadvorak.artilleryonline.battle.processor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.missile.CorrectingAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class MissileTargetProcessor implements BeforeStep1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getMissiles().forEach(missile -> CorrectingAccelerationCalculator.calculate(missile, battle));
    }
}
