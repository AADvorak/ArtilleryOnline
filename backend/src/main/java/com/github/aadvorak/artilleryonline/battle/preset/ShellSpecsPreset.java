package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShellSpecsPreset {

    HEAVY_AP("AP-H", new ShellSpecs()
            .setDamage(15.0)
            .setRadius(0.15)
            .setVelocity(13.0)
            .setMass(0.0061)
            .setCaliber(0.07)
            .setType(ShellType.AP)),
    HEAVY_HE("HE-H", new ShellSpecs()
            .setDamage(8.0)
            .setRadius(0.7)
            .setVelocity(12.0)
            .setMass(0.0061)
            .setCaliber(0.07)
            .setType(ShellType.HE)),
    HEAVY_AP_L("AP-HL", new ShellSpecs()
            .setDamage(10.0)
            .setRadius(0.1)
            .setVelocity(13.5)
            .setMass(0.002)
            .setCaliber(0.05)
            .setType(ShellType.AP)),
    HEAVY_HE_L("HE-HL", new ShellSpecs()
            .setDamage(5.0)
            .setRadius(0.5)
            .setVelocity(12.5)
            .setMass(0.002)
            .setCaliber(0.05)
            .setType(ShellType.HE)),
    MEDIUM_AP("AP-M", new ShellSpecs()
            .setDamage(10.0)
            .setRadius(0.1)
            .setVelocity(13.0)
            .setMass(0.002)
            .setCaliber(0.05)
            .setType(ShellType.AP)),
    MEDIUM_HE("HE-M", new ShellSpecs()
            .setDamage(5.0)
            .setRadius(0.5)
            .setVelocity(12.0)
            .setMass(0.002)
            .setCaliber(0.05)
            .setType(ShellType.HE)),
    MEDIUM_AP_L("AP-ML", new ShellSpecs()
            .setDamage(1.5)
            .setRadius(0.04)
            .setVelocity(13.0)
            .setMass(0.0002)
            .setCaliber(0.03)
            .setType(ShellType.AP)),
    LIGHT_AP("AP-L", new ShellSpecs()
            .setDamage(3.0)
            .setRadius(0.06)
            .setVelocity(12.0)
            .setMass(0.00025)
            .setCaliber(0.03)
            .setType(ShellType.AP)),
    LIGHT_HE("HE-L", new ShellSpecs()
            .setDamage(4.0)
            .setRadius(0.4)
            .setVelocity(11.0)
            .setMass(0.001)
            .setCaliber(0.04)
            .setType(ShellType.HE)),
    LIGHT_SGN("SGN-L", new ShellSpecs()
            .setVelocity(10.0)
            .setMass(0.0002)
            .setCaliber(0.07)
            .setType(ShellType.SGN)),
    BMB_H("BMB-H", new ShellSpecs()
            .setDamage(15.0)
            .setRadius(0.9)
            .setVelocity(11.0)
            .setMass(0.007)
            .setCaliber(0.12)
            .setType(ShellType.BMB)),
    BMB("BMB", new ShellSpecs()
            .setDamage(8.0)
            .setRadius(0.7)
            .setVelocity(5.0)
            .setMass(0.008)
            .setCaliber(0.15)
            .setType(ShellType.BMB));

    private final String name;

    private final ShellSpecs specs;
}
