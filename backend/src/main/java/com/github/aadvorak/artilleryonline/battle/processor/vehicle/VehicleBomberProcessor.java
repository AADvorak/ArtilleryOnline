package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleBomberProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var bomberState = vehicleModel.getState().getBomberState();
        if (bomberState == null || bomberState.isReadyToFlight()) {
            return;
        }
        bomberState.setPrepareToFlightRemainTime(bomberState.getPrepareToFlightRemainTime()
                - battleModel.getCurrentTimeStepSecs());
        if (bomberState.getPrepareToFlightRemainTime() <= 0) {
            bomberState.setReadyToFlight(true);
            vehicleModel.setUpdated(true);
        }
    }
}
