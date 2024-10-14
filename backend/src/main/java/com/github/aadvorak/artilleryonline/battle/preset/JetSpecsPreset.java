package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.JetSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JetSpecsPreset {

    DEFAULT("default", new JetSpecs()
            .setCapacity(100.0)
            .setConsumption(10.0)
            .setRegeneration(1.0)
            .setAcceleration(15.0));

    private final String name;

    private final JetSpecs specs;
}
