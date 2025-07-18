package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.Step2Processor;
import org.springframework.stereotype.Component;

@Component
public class VehicleMoveStep2Processor extends VehicleProcessor implements Step2Processor {

    @Override
    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.applyNextPosition();
        calculateOnGround(vehicle);
    }

    private void calculateOnGround(VehicleCalculations vehicle) {
        var state = vehicle.getModel().getState();
        var onGround = vehicle.getRightWheel().getGroundContact() != null
                || vehicle.getLeftWheel().getGroundContact() != null;
        if (state.isOnGround() != onGround) {
            state.setOnGround(onGround);
            vehicle.getModel().getUpdate().setUpdated();
        }
    }
}
