package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.MissileSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MissileSpecsPreset {

    DEFAULT("Default", new MissileSpecs()
            .setPushingAcceleration(14.0)
            .setCorrectingAccelerationCoefficient(1.0)
            .setMinCorrectingVelocity(2.0)
            .setDamage(10.0)
            .setRadius(0.5)
            .setMass(0.007)
            .setLength(0.5)
            .setCaliber(0.05)
    );

    private final String name;

    private final MissileSpecs specs;
}
