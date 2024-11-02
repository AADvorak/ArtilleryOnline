package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.JetSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JetSpecsPreset {

    LIGHT("light", new JetSpecs()
            .setCapacity(30.0)
            .setConsumption(15.0)
            .setRegeneration(1.0)
            .setAcceleration(13.0)),

    MEDIUM("medium", new JetSpecs()
            .setCapacity(30.0)
            .setConsumption(20.0)
            .setRegeneration(0.5)
            .setAcceleration(12.0));

    private final String name;

    private final JetSpecs specs;
}
