package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum GunSpecsPreset {

    HEAVY("Heavy", new GunSpecs()
            .setAmmo(60)
            .setLoadTime(5.0)
            .setRotationVelocity(0.35)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.53)
            .setCaliber(0.07)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.HEAVY_AP.getName(), ShellSpecsPreset.HEAVY_AP.getSpecs(),
                    ShellSpecsPreset.HEAVY_HE.getName(), ShellSpecsPreset.HEAVY_HE.getSpecs()
            ))),

    HEAVY_MORTAR("HeavyMortar", new GunSpecs()
            .setAmmo(30)
            .setLoadTime(9.0)
            .setRotationVelocity(0.3)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.53)
            .setCaliber(0.12)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.BMB_H.getName(), ShellSpecsPreset.BMB_H.getSpecs()
            ))),

    HEAVY_L("HeavyL", new GunSpecs()
            .setAmmo(80)
            .setLoadTime(3.0)
            .setRotationVelocity(0.4)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.55)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.HEAVY_AP_L.getName(), ShellSpecsPreset.HEAVY_AP_L.getSpecs(),
                    ShellSpecsPreset.HEAVY_HE_L.getName(), ShellSpecsPreset.HEAVY_HE_L.getSpecs()
            ))),

    MEDIUM("Medium", new GunSpecs()
            .setAmmo(80)
            .setLoadTime(3.0)
            .setRotationVelocity(0.4)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.47)
            .setCaliber(0.05)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP.getName(), ShellSpecsPreset.MEDIUM_AP.getSpecs(),
                    ShellSpecsPreset.MEDIUM_HE.getName(), ShellSpecsPreset.MEDIUM_HE.getSpecs()
            ))),

    MEDIUM_L("MediumL", new GunSpecs()
            .setAmmo(500)
            .setLoadTime(0.3)
            .setRotationVelocity(0.45)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.45)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.MEDIUM_AP_L.getName(), ShellSpecsPreset.MEDIUM_AP_L.getSpecs()
            ))),

    LIGHT("Light", new GunSpecs()
            .setAmmo(320)
            .setLoadTime(0.5)
            .setRotationVelocity(0.45)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.35)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_AP.getSpecs(),
                    ShellSpecsPreset.LIGHT_SGN.getName(), ShellSpecsPreset.LIGHT_SGN.getSpecs()
            ))),

    LIGHT_H("LightH", new GunSpecs()
            .setAmmo(100)
            .setLoadTime(2.2)
            .setRotationVelocity(0.42)
            .setSlowRotationVelocity(0.2)
            .setSlowToFastRotationTime(0.5)
            .setLength(0.4)
            .setCaliber(0.04)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.LIGHT_HE.getName(), ShellSpecsPreset.LIGHT_HE.getSpecs(),
                    ShellSpecsPreset.LIGHT_SGN.getName(), ShellSpecsPreset.LIGHT_SGN.getSpecs()
            ))),

    DRONE_LIGHT("DroneLight", new GunSpecs()
            .setAmmo(8)
            .setLoadTime(0.3)
            .setLength(0.2)
            .setCaliber(0.03)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.DRONE_AP.getName(), ShellSpecsPreset.DRONE_AP.getSpecs()
            ))),

    DRONE_HEAVY("DroneHeavy", new GunSpecs()
            .setAmmo(3)
            .setLoadTime(2.0)
            .setLength(0.25)
            .setCaliber(0.04)
            .setAvailableShells(Map.of(
                    ShellSpecsPreset.DRONE_HE.getName(), ShellSpecsPreset.DRONE_HE.getSpecs()
            )));

    private final String name;

    private final GunSpecs specs;
}
