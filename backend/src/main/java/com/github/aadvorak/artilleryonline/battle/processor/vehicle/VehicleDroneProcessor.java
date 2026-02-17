package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class VehicleDroneProcessor extends VehicleProcessor implements BeforeStep1Processor {

    @Override
    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var inVehicleState = vehicle.getModel().getState().getDroneState();
        if (inVehicleState == null || inVehicleState.isLaunched()
                || inVehicleState.isReadyToLaunch() || inVehicleState.getRemainDrones() < 1) {
            return;
        }
        var prepareToLaunchRemainTime = inVehicleState.getPrepareToLaunchRemainTime()
                - battle.getModel().getCurrentTimeStepSecs();
        if (prepareToLaunchRemainTime <= 0) {
            inVehicleState.setReadyToLaunch(true);
            vehicle.getModel().getUpdate().setUpdated();
        }
        inVehicleState.setPrepareToLaunchRemainTime(prepareToLaunchRemainTime);
    }
}
