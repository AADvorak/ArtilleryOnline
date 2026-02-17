package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.common.HitSurface;
import com.github.aadvorak.artilleryonline.battle.common.shapes.HalfCircleShape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum VehicleSpecsPreset {

    HEAVY("Heavy", new VehicleSpecs()
            .setName("Heavy")
            .setDrones(3)
            .setTurretShape(
                    new TrapezeShape()
                            .setBottomRadius(0.45)
                            .setTopRadius(0.3)
                            .setHeight(0.35)
            )
            .setArmor(
                    Map.of(
                            HitSurface.BOTTOM, 10.0,
                            HitSurface.TOP, 30.0,
                            HitSurface.SIDE, 50.0
                    )
            )
            .setHullRadius(0.5)
            .setWheelRadius(0.12)
            .setTrackRepairTime(15.0)
            .setMinTrackHitCaliber(0.06)
            .setHitPoints(120.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(7.0)
            .setWheelAngleVelocity(18.0)
            .setAvailableGuns(Map.of(
                    GunSpecsPreset.HEAVY.getName(), GunSpecsPreset.HEAVY.getSpecs(),
                    GunSpecsPreset.HEAVY_L.getName(), GunSpecsPreset.HEAVY_L.getSpecs(),
                    GunSpecsPreset.HEAVY_MORTAR.getName(), GunSpecsPreset.HEAVY_MORTAR.getSpecs()
            ))
            .setAvailableJets(Map.of(JetSpecsPreset.HEAVY.getName(), JetSpecsPreset.HEAVY.getSpecs()))
            .setAvailableMissileLaunchers(Map.of())
            .setAvailableDrones(Map.of(DroneSpecsPreset.LIGHT.getName(), DroneSpecsPreset.LIGHT.getSpecs()))
            .setAvailableBombers(Map.of())
            .setDefaultGun(GunSpecsPreset.HEAVY.getName())
            .setMinCollisionDamageImpact(0.7)
            .setCollisionDamageCoefficient(2.5)
    ),

    MEDIUM("Medium", new VehicleSpecs()
            .setName("Medium")
            .setMissiles(5)
            .setTurretShape(
                    new TrapezeShape()
                            .setBottomRadius(0.4)
                            .setTopRadius(0.25)
                            .setHeight(0.3)
            )
            .setArmor(
                    Map.of(
                            HitSurface.BOTTOM, 10.0,
                            HitSurface.TOP, 25.0,
                            HitSurface.SIDE, 40.0
                    )
            )
            .setHullRadius(0.4)
            .setWheelRadius(0.1)
            .setTrackRepairTime(10.0)
            .setMinTrackHitCaliber(0.04)
            .setHitPoints(100.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(12.0)
            .setWheelAngleVelocity(30.0)
            .setAvailableGuns(Map.of(
                    GunSpecsPreset.MEDIUM.getName(), GunSpecsPreset.MEDIUM.getSpecs(),
                    GunSpecsPreset.MEDIUM_L.getName(), GunSpecsPreset.MEDIUM_L.getSpecs()
            ))
            .setAvailableJets(Map.of(JetSpecsPreset.MEDIUM.getName(), JetSpecsPreset.MEDIUM.getSpecs()))
            .setAvailableMissileLaunchers(Map.of(MissileLauncherSpecsPreset.DEFAULT.getName(), MissileLauncherSpecsPreset.DEFAULT.getSpecs()))
            .setAvailableDrones(Map.of())
            .setAvailableBombers(Map.of())
            .setDefaultGun(GunSpecsPreset.MEDIUM.getName())
            .setMinCollisionDamageImpact(0.5)
            .setCollisionDamageCoefficient(3.5)
    ),

    LIGHT("Light", new VehicleSpecs()
            .setName("Light")
            .setTurretShape(
                    new HalfCircleShape()
                            .setRadius(0.3)
            )
            .setArmor(
                    Map.of(
                            HitSurface.BOTTOM, 8.0,
                            HitSurface.TOP, 30.0,
                            HitSurface.SIDE, 30.0
                    )
            )
            .setHullRadius(0.35)
            .setWheelRadius(0.08)
            .setTrackRepairTime(7.0)
            .setMinTrackHitCaliber(0.02)
            .setHitPoints(80.0)
            .setMinAngle(0.0)
            .setMaxAngle(Math.PI)
            .setAcceleration(17.0)
            .setWheelAngleVelocity(40.0)
            .setAvailableGuns(Map.of(
                    GunSpecsPreset.LIGHT.getName(), GunSpecsPreset.LIGHT.getSpecs(),
                    GunSpecsPreset.LIGHT_H.getName(), GunSpecsPreset.LIGHT_H.getSpecs()
            ))
            .setAvailableJets(Map.of(JetSpecsPreset.LIGHT.getName(), JetSpecsPreset.LIGHT.getSpecs()))
            .setAvailableMissileLaunchers(Map.of())
            .setAvailableDrones(Map.of())
            .setAvailableBombers(Map.of(BomberSpecsPreset.DEFAULT.getName(), BomberSpecsPreset.DEFAULT.getSpecs()))
            .setDefaultGun(GunSpecsPreset.LIGHT.getName())
            .setMinCollisionDamageImpact(0.3)
            .setCollisionDamageCoefficient(4.5)
    );

    private final String name;

    private final VehicleSpecs specs;
}
