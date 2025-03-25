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
            .setRadius(0.45)
            .setHullRadius(0.5)
            .setWheelRadius(0.12)
            .setTrackRepairTime(15.0)
            .setMinTrackHitCaliber(0.06)
            .setHitPoints(120.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(6.0)
            .setAvailableGuns(Map.of(
                    GunSpecsPreset.HEAVY.getName(), GunSpecsPreset.HEAVY.getSpecs(),
                    GunSpecsPreset.HEAVY_L.getName(), GunSpecsPreset.HEAVY_L.getSpecs(),
                    GunSpecsPreset.HEAVY_MORTAR.getName(), GunSpecsPreset.HEAVY_MORTAR.getSpecs()
            ))
            .setAvailableJets(Map.of(JetSpecsPreset.HEAVY.getName(), JetSpecsPreset.HEAVY.getSpecs()))
            .setAvailableMissiles(Map.of())
            .setAvailableDrones(Map.of(DroneSpecsPreset.LIGHT.getName(), DroneSpecsPreset.LIGHT.getSpecs()))
            .setAvailableBombers(Map.of())
            .setDefaultGun(GunSpecsPreset.HEAVY.getName())
            .setMinCollisionDamageImpact(0.2)
            .setCollisionDamageCoefficient(50.0)
    ),

    MEDIUM("Medium", new VehicleSpecs()
            .setName("Medium")
            .setMissiles(4)
            .setRadius(0.4)
            .setHullRadius(0.4)
            .setWheelRadius(0.1)
            .setTrackRepairTime(10.0)
            .setMinTrackHitCaliber(0.04)
            .setHitPoints(100.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(10.0)
            .setAvailableGuns(Map.of(
                    GunSpecsPreset.MEDIUM.getName(), GunSpecsPreset.MEDIUM.getSpecs(),
                    GunSpecsPreset.MEDIUM_L.getName(), GunSpecsPreset.MEDIUM_L.getSpecs()
            ))
            .setAvailableJets(Map.of(JetSpecsPreset.MEDIUM.getName(), JetSpecsPreset.MEDIUM.getSpecs()))
            .setAvailableMissiles(Map.of(MissileSpecsPreset.DEFAULT.getName(), MissileSpecsPreset.DEFAULT.getSpecs()))
            .setAvailableDrones(Map.of())
            .setAvailableBombers(Map.of())
            .setDefaultGun(GunSpecsPreset.MEDIUM.getName())
            .setMinCollisionDamageImpact(0.15)
            .setCollisionDamageCoefficient(60.0)
    ),

    LIGHT("Light", new VehicleSpecs()
            .setName("Light")
            .setRadius(0.3)
            .setHullRadius(0.35)
            .setWheelRadius(0.08)
            .setTrackRepairTime(7.0)
            .setMinTrackHitCaliber(0.02)
            .setHitPoints(80.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(14.0)
            .setAvailableGuns(Map.of(
                    GunSpecsPreset.LIGHT.getName(), GunSpecsPreset.LIGHT.getSpecs(),
                    GunSpecsPreset.LIGHT_H.getName(), GunSpecsPreset.LIGHT_H.getSpecs()
            ))
            .setAvailableJets(Map.of(JetSpecsPreset.LIGHT.getName(), JetSpecsPreset.LIGHT.getSpecs()))
            .setAvailableMissiles(Map.of())
            .setAvailableDrones(Map.of())
            .setAvailableBombers(Map.of(BomberSpecsPreset.DEFAULT.getName(), BomberSpecsPreset.DEFAULT.getSpecs()))
            .setDefaultGun(GunSpecsPreset.LIGHT.getName())
            .setMinCollisionDamageImpact(0.12)
            .setCollisionDamageCoefficient(70.0)
    );

    private final String name;

    private final VehicleSpecs specs;
}
