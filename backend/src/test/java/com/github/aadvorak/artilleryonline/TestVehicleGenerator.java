package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.specs.JetSpecs;
import com.github.aadvorak.artilleryonline.battle.state.GunState;
import com.github.aadvorak.artilleryonline.battle.state.JetState;
import com.github.aadvorak.artilleryonline.battle.state.TrackState;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.Arrays;
import java.util.HashMap;

public class TestVehicleGenerator {

    public static VehicleModel generate(String name) {
        var vehicleModel = new VehicleModel();
        vehicleModel.setSpecs(Arrays.stream(VehicleSpecsPreset.values())
                .filter(preset -> preset.getName().equals(name))
                .map(VehicleSpecsPreset::getSpecs).findAny().orElseThrow());
        vehicleModel.setPreCalc(new VehiclePreCalc(vehicleModel.getSpecs()));
        var gun = vehicleModel.getSpecs().getAvailableGuns().values().iterator().next();
        JetSpecs jet = null;
        if (!vehicleModel.getSpecs().getAvailableJets().isEmpty()) {
            jet = vehicleModel.getSpecs().getAvailableJets().values().iterator().next();
        }
        var availableShellsNumber = gun.getAvailableShells().size();
        var ammo = new HashMap<String, Integer>();
        vehicleModel.setConfig(new VehicleConfig()
                .setAmmo(ammo)
                .setGun(gun)
                .setJet(jet)
        );
        gun.getAvailableShells().keySet().forEach(shellName ->
                ammo.put(shellName, vehicleModel.getConfig().getGun().getAmmo() / availableShellsNumber));
        vehicleModel.setState(new VehicleState()
                .setAmmo(new HashMap<>(vehicleModel.getConfig().getAmmo()))
                .setHitPoints(vehicleModel.getSpecs().getHitPoints())
                .setGunState(
                        new GunState()
                                .setSelectedShell(ammo.keySet().stream().findAny().orElseThrow())
                )
                .setTrackState(new TrackState())
                .setJetState(
                        jet == null ? null : new JetState()
                                .setVolume(jet.getCapacity())
                )
        );
        return vehicleModel;
    }
}
