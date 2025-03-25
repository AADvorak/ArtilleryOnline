package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.config.DroneConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;
import com.github.aadvorak.artilleryonline.battle.state.GunState;

import java.util.HashMap;
import java.util.Map;

public class VehicleLaunchDroneProcessor {

    public static void launch(VehicleModel vehicleModel, BattleModel battleModel) {
        if (vehicleModel.getConfig().getDrone() == null) {
            return;
        }
        var inVehicleState = vehicleModel.getState().getDroneState();
        if (inVehicleState == null || !inVehicleState.isReadyToLaunch()) {
            return;
        }
        var specs = vehicleModel.getConfig().getDrone();
        var vehiclePosition = vehicleModel.getState().getPosition();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var angle = vehiclePosition.getAngle();
        var gunSpecs = specs.getAvailableGuns().values().iterator().next();
        var shellName = gunSpecs.getAvailableShells().keySet().iterator().next();
        var config = new DroneConfig()
                .setGun(gunSpecs)
                .setAmmo(Map.of(shellName, gunSpecs.getAmmo()))
                .setColor(vehicleModel.getConfig().getColor());
        var state = new DroneState()
                .setPosition(BodyPosition.of(vehiclePosition.getCenter().shifted(vehicleRadius, angle + Math.PI / 2), angle))
                .setVelocity(BodyVelocity.of(vehicleModel.getState().getVelocity()))
                .setAmmo(new HashMap<>(config.getAmmo()))
                .setGunState(new GunState()
                        .setSelectedShell(shellName)
                        .setTriggerPushed(false))
                .setGunAngle(- Math.PI / 2);
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
        inVehicleState.setReadyToLaunch(false);
        inVehicleState.setLaunched(true);
    }
}
