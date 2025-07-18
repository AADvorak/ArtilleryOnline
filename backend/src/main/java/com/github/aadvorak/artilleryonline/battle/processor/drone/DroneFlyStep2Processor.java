package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.Step2Processor;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

@Component
public class DroneFlyStep2Processor implements Step2Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getDrones().forEach(drone -> processDrone(drone, battle.getModel()));
    }

    private void processDrone(DroneCalculations drone, BattleModel battleModel) {
        drone.applyNextPosition();
        if (drone.getPosition().getY() > 1.5 * BattleUtils.getRoomHeight(battleModel.getRoom().getSpecs())) {
            battleModel.getUpdates().removeDrone(drone.getId());
        }
    }
}
