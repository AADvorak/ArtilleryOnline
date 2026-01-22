package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.MissileLauncherSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MissileLauncherSpecsPreset {

    DEFAULT("MissileLauncher", new MissileLauncherSpecs()
            .setPrepareToLaunchTime(1.0)
            .setMissiles(MissileSpecsPreset.DEFAULT.getSpecs())
    );

    private final String name;

    private final MissileLauncherSpecs specs;
}
