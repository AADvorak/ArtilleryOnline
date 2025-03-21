package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum GunSpecsPreset {

    HEAVY("Heavy gun", new GunSpecs()
            .setLoadTime(5.0)
            .setRotationVelocity(0.35)
            .setLength(0.6)
            .setCaliber(0.07)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.HEAVY_AP.getName(), ShellSpecsPreset.HEAVY_AP.getSpecs(),
                    ShellSpecsPreset.HEAVY_HE.getName(), ShellSpecsPreset.HEAVY_HE.getSpecs()
            ))),

    MEDIUM("Medium gun", new GunSpecs()
            .setLoadTime(3.0)
            .setRotationVelocity(0.4)
            .setLength(0.6)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP.getName(), ShellSpecsPreset.MEDIUM_AP.getSpecs(),
                    ShellSpecsPreset.MEDIUM_HE.getName(), ShellSpecsPreset.MEDIUM_HE.getSpecs()
            ))),

    LIGHT("Light gun", new GunSpecs()
            .setLoadTime(0.5)
            .setRotationVelocity(0.45)
            .setLength(0.4)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs(),
                    ShellSpecsPreset.LIGHT_SGN.getName(), ShellSpecsPreset.LIGHT_SGN.getSpecs()
            ))),

    DRONE_LIGHT("Drone light gun", new GunSpecs()
            .setLoadTime(1.0)
            .setRotationVelocity(0.0)
            .setLength(0.2)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs()
            ))),

    DRONE_HEAVY("Drone heavy gun", new GunSpecs()
            .setLoadTime(3.0)
            .setRotationVelocity(0.0)
            .setLength(0.25)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_HE.getName(), ShellSpecsPreset.MEDIUM_HE.getSpecs()
            )));

    private final String name;

    private final GunSpecs specs;
}
