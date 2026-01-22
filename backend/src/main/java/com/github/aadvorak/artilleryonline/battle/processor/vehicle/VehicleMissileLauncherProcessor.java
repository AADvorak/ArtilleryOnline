package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class VehicleMissileLauncherProcessor extends VehicleProcessor implements BeforeStep1Processor {

    @Override
    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var state = vehicle.getModel().getState().getMissileLauncherState();
        if (state != null && state.getRemainMissiles() > 0 && state.getPrepareToLaunchRemainTime() > 0) {
            state.setPrepareToLaunchRemainTime(state.getPrepareToLaunchRemainTime()
                    - battle.getModel().getCurrentTimeStepSecs());
        }
    }
}
