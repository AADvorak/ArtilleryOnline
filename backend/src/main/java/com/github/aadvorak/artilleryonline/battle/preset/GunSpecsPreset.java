package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum GunSpecsPreset {

    DEFAULT("default", new GunSpecs()
            .setLoadTime(5.0)
            .setRotationVelocity(0.2)
            .setLength(0.8)
            .setAvailableShells(Map.of(ShellSpecsPreset.DEFAULT.getName(), ShellSpecsPreset.DEFAULT.getSpecs())));

    private final String name;

    private final GunSpecs specs;
}
