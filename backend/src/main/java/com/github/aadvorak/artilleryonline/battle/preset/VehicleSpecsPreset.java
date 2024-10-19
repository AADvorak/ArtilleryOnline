package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum VehicleSpecsPreset {

    DEFAULT("default", new VehicleSpecs()
            .setAmmo(80)
            .setRadius(0.4)
            .setWheelRadius(0.1)
            .setTrackRepairTime(10.0)
            .setHitPoints(100.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setCriticalAngle(Math.PI / 4)
            .setMovingVelocity(4.0)
            .setAcceleration(10.0)
            .setAvailableGuns(Map.of(GunSpecsPreset.DEFAULT.getName(), GunSpecsPreset.DEFAULT.getSpecs())));

    private final String name;

    private final VehicleSpecs specs;
}
