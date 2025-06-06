package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.MissileSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MissileSpecsPreset {

    DEFAULT("MSL", new MissileSpecs()
            .setPushingAcceleration(18.0)
            .setCorrectingAccelerationCoefficient(5.0)
            .setMinCorrectingVelocity(5.0)
            .setAnglePrecision(Math.PI / 8)
            .setDamage(6.0)
            .setRadius(0.6)
            .setMass(0.07)
            .setLength(0.5)
            .setCaliber(0.05)
    );

    private final String name;

    private final MissileSpecs specs;
}
