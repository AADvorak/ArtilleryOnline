package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum GunSpecsPreset {

    HEAVY("Heavy", new GunSpecs()
            .setLoadTime(5.0)
            .setRotationVelocity(0.35)
            .setLength(0.6)
            .setCaliber(0.07)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.HEAVY_AP.getName(), ShellSpecsPreset.HEAVY_AP.getSpecs(),
                    ShellSpecsPreset.HEAVY_HE.getName(), ShellSpecsPreset.HEAVY_HE.getSpecs()
            ))),

    MEDIUM("Medium", new GunSpecs()
            .setLoadTime(3.0)
            .setRotationVelocity(0.4)
            .setLength(0.6)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP.getName(), ShellSpecsPreset.MEDIUM_AP.getSpecs(),
                    ShellSpecsPreset.MEDIUM_HE.getName(), ShellSpecsPreset.MEDIUM_HE.getSpecs()
            ))),

    LIGHT("Light", new GunSpecs()
            .setLoadTime(0.5)
            .setRotationVelocity(0.45)
            .setLength(0.4)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs()
            ))),

    DRONE("Drone", new GunSpecs()
            .setLoadTime(0.5)
            .setRotationVelocity(0.0)
            .setLength(0.4)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs()
            )));

    private final String name;

    private final GunSpecs specs;
}
