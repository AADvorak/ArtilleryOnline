package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;

public abstract class VehicleProcessor {
    protected abstract void processVehicle(VehicleCalculations vehicle, BattleCalculations battle);

    public void process(BattleCalculations battle) {
        battle.getVehicles().forEach(vehicle -> processVehicle(vehicle, battle));
    }
}
