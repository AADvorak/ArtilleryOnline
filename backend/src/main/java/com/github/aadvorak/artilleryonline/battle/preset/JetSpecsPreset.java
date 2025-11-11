package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.common.JetType;
import com.github.aadvorak.artilleryonline.battle.specs.JetSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JetSpecsPreset {

    LIGHT("light", new JetSpecs()
            .setCapacity(45.0)
            .setConsumption(15.0)
            .setRegeneration(1.0)
            .setAcceleration(18.0)
            .setType(JetType.VERTICAL)
    ),

    MEDIUM("medium", new JetSpecs()
            .setCapacity(45.0)
            .setConsumption(20.0)
            .setRegeneration(1.0)
            .setAcceleration(12.0)
            .setType(JetType.HORIZONTAL)
    ),

    HEAVY("heavy", new JetSpecs()
            .setCapacity(45.0)
            .setConsumption(20.0)
            .setRegeneration(1.0)
            .setAcceleration(10.0)
            .setType(JetType.HORIZONTAL)
    );

    private final String name;

    private final JetSpecs specs;
}
