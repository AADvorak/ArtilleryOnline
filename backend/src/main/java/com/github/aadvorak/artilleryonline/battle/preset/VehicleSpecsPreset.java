package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum VehicleSpecsPreset {

    HEAVY("Heavy", new VehicleSpecs()
            .setName("Heavy")
            .setAmmo(60)
            .setRadius(0.45)
            .setHullRadius(0.5)
            .setWheelRadius(0.12)
            .setTrackRepairTime(15.0)
            .setMinTrackHitCaliber(0.06)
            .setHitPoints(120.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(6.0)
            .setAvailableGuns(Map.of(GunSpecsPreset.HEAVY.getName(), GunSpecsPreset.HEAVY.getSpecs()))
            .setAvailableJets(Map.of())),

    MEDIUM("Medium", new VehicleSpecs()
            .setName("Medium")
            .setAmmo(80)
            .setRadius(0.4)
            .setHullRadius(0.4)
            .setWheelRadius(0.1)
            .setTrackRepairTime(10.0)
            .setMinTrackHitCaliber(0.04)
            .setHitPoints(100.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(10.0)
            .setAvailableGuns(Map.of(GunSpecsPreset.MEDIUM.getName(), GunSpecsPreset.MEDIUM.getSpecs()))
            .setAvailableJets(Map.of(JetSpecsPreset.MEDIUM.getName(), JetSpecsPreset.MEDIUM.getSpecs()))),

    LIGHT("Light", new VehicleSpecs()
            .setName("Light")
            .setAmmo(320)
            .setRadius(0.3)
            .setHullRadius(0.35)
            .setWheelRadius(0.08)
            .setTrackRepairTime(7.0)
            .setMinTrackHitCaliber(0.02)
            .setHitPoints(80.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(14.0)
            .setAvailableGuns(Map.of(GunSpecsPreset.LIGHT.getName(), GunSpecsPreset.LIGHT.getSpecs()))
            .setAvailableJets(Map.of(JetSpecsPreset.LIGHT.getName(), JetSpecsPreset.LIGHT.getSpecs())));

    private final String name;

    private final VehicleSpecs specs;
}
