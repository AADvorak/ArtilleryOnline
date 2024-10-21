package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum GunSpecsPreset {

    MEDIUM("Medium", new GunSpecs()
            .setLoadTime(3.0)
            .setRotationVelocity(0.2)
            .setLength(0.6)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP.getName(), ShellSpecsPreset.MEDIUM_AP.getSpecs(),
                    ShellSpecsPreset.MEDIUM_HE.getName(), ShellSpecsPreset.MEDIUM_HE.getSpecs()
            ))),

    LIGHT("Light", new GunSpecs()
            .setLoadTime(2.0)
            .setRotationVelocity(0.3)
            .setLength(0.4)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs(),
                    ShellSpecsPreset.LIGHT_HE.getName(), ShellSpecsPreset.LIGHT_HE.getSpecs()
            )));

    private final String name;

    private final GunSpecs specs;
}
