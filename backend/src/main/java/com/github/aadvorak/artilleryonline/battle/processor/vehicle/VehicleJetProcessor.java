package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleJetProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var jetSpecs = vehicleModel.getConfig().getJet();
        if (jetSpecs == null) {
            return;
        }
        var jetState = vehicleModel.getState().getJetState();
        if (jetState.isActive() && jetState.getVolume() > 0) {
            var volume = jetState.getVolume() - battleModel.getCurrentTimeStepSecs() * jetSpecs.getConsumption();
            jetState.setVolume(volume > 0 ? volume : 0);
            if (volume <= 0) {
                vehicleModel.setUpdated(true);
            }
        }
        if (!jetState.isActive() && jetState.getVolume() < jetSpecs.getCapacity()) {
            var volume = jetState.getVolume() + battleModel.getCurrentTimeStepSecs() * jetSpecs.getRegeneration();
            jetState.setVolume(Math.min(volume, jetSpecs.getCapacity()));
            if (volume >= jetSpecs.getCapacity()) {
                vehicleModel.setUpdated(true);
            }
        }
    }
}
