package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.DroneTargetCalculator;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class DroneTargetProcessor implements BeforeStep1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getDrones().forEach(drone -> DroneTargetCalculator.calculate(drone, battle));
    }
}
