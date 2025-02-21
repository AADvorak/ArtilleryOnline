package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.config.DroneConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.DroneSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.GunSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;

import java.util.Map;

public class VehicleLaunchDroneProcessor {

    public static void launch(VehicleModel vehicleModel, BattleModel battleModel) {
        var vehiclePosition = vehicleModel.getState().getPosition();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var angle = vehiclePosition.getAngle();
        var specs = DroneSpecsPreset.DEFAULT.getSpecs();
        var config = new DroneConfig()
                .setGun(GunSpecsPreset.DRONE.getSpecs())
                .setAmmo(Map.of());
        var state = new DroneState()
                .setPosition(BodyPosition.of(vehiclePosition.getCenter().shifted(vehicleRadius, angle + Math.PI / 2), angle))
                .setVelocity(BodyVelocity.of(vehicleModel.getState().getVelocity()))
                // todo
                .setAmmo(Map.of(ShellSpecsPreset.LIGHT_AP.getName(), specs.getAmmo()));
        var id = battleModel.getIdGenerator().generate();
        var model = new DroneModel();
        model.setId(id);
        model.setVehicleId(vehicleModel.getId());
        model.setUserId(vehicleModel.getUserId());
        model.setState(state);
        model.setConfig(config);
        model.setSpecs(specs);
        battleModel.getDrones().put(id, model);
        battleModel.getUpdates().addDrone(model);
        vehicleModel.setUpdated(true);
    }
}
