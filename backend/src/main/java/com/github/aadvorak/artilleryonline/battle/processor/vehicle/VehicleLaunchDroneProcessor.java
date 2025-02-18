package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.DroneSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;

public class VehicleLaunchDroneProcessor {

    public static void launch(VehicleModel vehicleModel, BattleModel battleModel) {
        var vehiclePosition = vehicleModel.getState().getPosition();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var angle = vehiclePosition.getAngle() + Math.PI / 2;
        var specs = DroneSpecsPreset.DEFAULT.getSpecs();
        var state = new DroneState()
                .setPosition(BodyPosition.of(vehiclePosition.getCenter().shifted(vehicleRadius, angle), angle))
                .setVelocity(BodyVelocity.of(vehicleModel.getState().getVelocity()));
        var id = battleModel.getIdGenerator().generate();
        var model = new DroneModel();
        model.setId(id);
        model.setVehicleId(vehicleModel.getId());
        model.setUserId(vehicleModel.getUserId());
        model.setState(state);
        model.setSpecs(specs);
        battleModel.getDrones().put(id, model);
        battleModel.getUpdates().addDrone(model);
        vehicleModel.setUpdated(true);
    }
}
