package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleTrackProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var trackState = vehicleModel.getState().getTrackState();
        if (trackState.isBroken()) {
            trackState.setRepairRemainTime(trackState.getRepairRemainTime() - battleModel.getCurrentTimeStepSecs());
        }
        if (trackState.getRepairRemainTime() <= 0) {
            trackState.setBroken(false);
            trackState.setRepairRemainTime(0.0);
            battleModel.setUpdated(true);
        }
    }
}
