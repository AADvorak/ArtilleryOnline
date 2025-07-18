package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.events.RepairEvent;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class VehicleTrackProcessor extends VehicleProcessor implements BeforeStep1Processor {

    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var trackState = vehicle.getModel().getState().getTrackState();
        if (trackState.isBroken()) {
            trackState.setRepairRemainTime(trackState.getRepairRemainTime() - battle.getModel().getCurrentTimeStepSecs());
            if (trackState.getRepairRemainTime() <= 0) {
                trackState.setBroken(false);
                trackState.setRepairRemainTime(0.0);
                vehicle.getModel().getUpdate().setUpdated();
                battle.getModel().getEvents().addRepair(new RepairEvent().setVehicleId(vehicle.getModel().getId()));
            }
        }
    }
}
