package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.JetType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleJetProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var jetSpecs = vehicleModel.getConfig().getJet();
        if (jetSpecs == null) {
            return;
        }
        var jetState = vehicleModel.getState().getJetState();
        var active = isJetActive(vehicleModel);
        if (active && jetState.getVolume() > 0) {
            var volume = jetState.getVolume() - battleModel.getCurrentTimeStepSecs() * jetSpecs.getConsumption();
            jetState.setVolume(volume > 0 ? volume : 0);
            if (volume <= 0) {
                vehicleModel.setUpdated(true);
            }
        }
        if (!active && jetState.getVolume() < jetSpecs.getCapacity()) {
            var volume = jetState.getVolume() + battleModel.getCurrentTimeStepSecs() * jetSpecs.getRegeneration();
            jetState.setVolume(Math.min(volume, jetSpecs.getCapacity()));
            if (volume >= jetSpecs.getCapacity()) {
                vehicleModel.setUpdated(true);
            }
        }
    }

    private static boolean isJetActive(VehicleModel vehicleModel) {
        var jetState = vehicleModel.getState().getJetState();
        var jetType = vehicleModel.getConfig().getJet().getType();
        if (JetType.VERTICAL.equals(jetType)) {
            return jetState.isActive();
        }
        var movingDirection = vehicleModel.getState().getMovingDirection();
        return jetState.isActive() && movingDirection != null;
    }
}
