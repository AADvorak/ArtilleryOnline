package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.DroneSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum DroneSpecsPreset {

    LIGHT("Light", new DroneSpecs()
            .setMass(0.02)
            .setAvailableGuns(Map.of(GunSpecsPreset.DRONE_LIGHT.getName(), GunSpecsPreset.DRONE_LIGHT.getSpecs()))
            .setHullRadius(0.1)
            .setEnginesRadius(0.2)
            .setFlyHeight(7.0)
            .setCriticalAngle(Math.PI / 20)
            .setMaxEngineAcceleration(12.0)
            .setPrepareToLaunchTime(20.0)
            .setMinCollisionDestroyImpact(0.1)
    ),

    HEAVY("Heavy", new DroneSpecs()
            .setMass(0.05)
            .setAvailableGuns(Map.of(GunSpecsPreset.DRONE_HEAVY.getName(), GunSpecsPreset.DRONE_HEAVY.getSpecs()))
            .setHullRadius(0.12)
            .setEnginesRadius(0.22)
            .setFlyHeight(9.0)
            .setCriticalAngle(Math.PI / 25)
            .setMaxEngineAcceleration(11.0)
            .setPrepareToLaunchTime(20.0)
            .setMinCollisionDestroyImpact(0.12)
    );

    private final String name;

    private final DroneSpecs specs;
}
