package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShellSpecsPreset {

    DEFAULT("default", new ShellSpecs()
            .setDamage(1.0)
            .setRadius(0.1)
            .setVelocity(1.0));

    private final String name;

    private final ShellSpecs specs;
}
