package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import com.github.aadvorak.artilleryonline.battle.specs.JetSpecs;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleConfig implements Config {

    private GunSpecs gun;

    private JetSpecs jet;

    private Map<String, Integer> ammo;
}
