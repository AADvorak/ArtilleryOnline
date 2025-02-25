package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.DroneSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum DroneSpecsPreset {

    DEFAULT("Drone", new DroneSpecs()
            .setAmmo(4)
            .setMass(0.002)
            .setAvailableGuns(Map.of(GunSpecsPreset.DRONE.getName(), GunSpecsPreset.DRONE.getSpecs()))
            .setHullRadius(0.1)
            .setEnginesRadius(0.2)
            .setFlyHeight(5.0)
            .setCriticalAngle(Math.PI / 16)
            .setMaxEngineAcceleration(12.0)
            .setPrepareToLaunchTime(20.0)
    );

    private final String name;

    private final DroneSpecs specs;
}
