package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum VehicleSpecsPreset {

    DEFAULT("default", new VehicleSpecs()
            .setAmmo(30)
            .setRadius(0.5)
            .setHitPoints(10.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setMovingVelocity(0.1)
            .setAvailableGuns(Map.of(GunSpecsPreset.DEFAULT.getName(), GunSpecsPreset.DEFAULT.getSpecs())));

    private final String name;

    private final VehicleSpecs specs;
}
