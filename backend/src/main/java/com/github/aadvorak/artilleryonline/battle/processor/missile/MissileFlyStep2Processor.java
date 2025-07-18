package com.github.aadvorak.artilleryonline.battle.processor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.Step2Processor;
import org.springframework.stereotype.Component;

@Component
public class MissileFlyStep2Processor implements Step2Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getMissiles().forEach(MissileCalculations::applyNextPosition);
    }
}
