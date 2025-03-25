package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum GunSpecsPreset {

    HEAVY("Heavy gun", new GunSpecs()
            .setAmmo(60)
            .setLoadTime(5.0)
            .setRotationVelocity(0.35)
            .setLength(0.6)
            .setCaliber(0.07)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.HEAVY_AP.getName(), ShellSpecsPreset.HEAVY_AP.getSpecs(),
                    ShellSpecsPreset.HEAVY_HE.getName(), ShellSpecsPreset.HEAVY_HE.getSpecs()
            ))),

    HEAVY_MORTAR("Heavy mortar", new GunSpecs()
            .setAmmo(30)
            .setLoadTime(10.0)
            .setRotationVelocity(0.3)
            .setLength(0.6)
            .setCaliber(0.12)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.BMB_H.getName(), ShellSpecsPreset.BMB_H.getSpecs()
            ))),

    HEAVY_L("Heavy light gun", new GunSpecs()
            .setAmmo(80)
            .setLoadTime(3.0)
            .setRotationVelocity(0.4)
            .setLength(0.65)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.HEAVY_AP_L.getName(), ShellSpecsPreset.HEAVY_AP_L.getSpecs(),
                    ShellSpecsPreset.HEAVY_HE_L.getName(), ShellSpecsPreset.HEAVY_HE_L.getSpecs()
            ))),

    MEDIUM("Medium gun", new GunSpecs()
            .setAmmo(80)
            .setLoadTime(3.0)
            .setRotationVelocity(0.4)
            .setLength(0.6)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP.getName(), ShellSpecsPreset.MEDIUM_AP.getSpecs(),
                    ShellSpecsPreset.MEDIUM_HE.getName(), ShellSpecsPreset.MEDIUM_HE.getSpecs()
            ))),

    MEDIUM_L("Medium light gun", new GunSpecs()
            .setAmmo(360)
            .setLoadTime(0.25)
            .setRotationVelocity(0.45)
            .setLength(0.5)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP_L.getName(), ShellSpecsPreset.MEDIUM_AP_L.getSpecs()
            ))),

    LIGHT("Light gun", new GunSpecs()
            .setAmmo(320)
            .setLoadTime(0.5)
            .setRotationVelocity(0.45)
            .setLength(0.4)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs(),
                    ShellSpecsPreset.LIGHT_SGN.getName(), ShellSpecsPreset.LIGHT_SGN.getSpecs()
            ))),

    DRONE_LIGHT("Drone light gun", new GunSpecs()
            .setAmmo(4)
            .setLoadTime(1.0)
            .setRotationVelocity(0.0)
            .setLength(0.2)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs()
            ))),

    DRONE_HEAVY("Drone heavy gun", new GunSpecs()
            .setAmmo(1)
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
