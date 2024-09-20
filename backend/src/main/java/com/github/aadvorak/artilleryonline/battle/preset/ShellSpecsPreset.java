package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShellSpecsPreset {

    DEFAULT_AP("AP", new ShellSpecs()
            .setDamage(10.0)
            .setRadius(0.1)
            .setVelocity(13.0)
            .setType(ShellType.AP)),
    DEFAULT_HE("HE", new ShellSpecs()
            .setDamage(5.0)
            .setRadius(0.5)
            .setVelocity(12.0)
            .setType(ShellType.HE));

    private final String name;

    private final ShellSpecs specs;
}
