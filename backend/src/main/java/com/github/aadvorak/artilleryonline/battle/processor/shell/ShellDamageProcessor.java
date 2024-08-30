package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;

public class ShellDamageProcessor {

    public static void process(VehicleModel vehicleModel, ShellSpecs shellSpecs, BattleModel battleModel) {
        var heatPoints = vehicleModel.getState().getHeatPoints();
        heatPoints -= shellSpecs.getDamage();
        if (heatPoints <= 0) {
            battleModel.removeVehicleById(vehicleModel.getId());
        } else {
            vehicleModel.getState().setHeatPoints(heatPoints);
        }
    }
}
