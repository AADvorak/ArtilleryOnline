package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleDroneProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var inVehicleState = vehicleModel.getState().getDroneState();
        if (inVehicleState == null || inVehicleState.isLaunched() || inVehicleState.isReadyToLaunch()) {
            return;
        }
        var prepareToLaunchRemainTime = inVehicleState.getPrepareToLaunchRemainTime() - battleModel.getCurrentTimeStepSecs();
        if (prepareToLaunchRemainTime <= 0) {
            inVehicleState.setReadyToLaunch(true);
            vehicleModel.setUpdated(true);
        }
        inVehicleState.setPrepareToLaunchRemainTime(prepareToLaunchRemainTime);
    }
}
