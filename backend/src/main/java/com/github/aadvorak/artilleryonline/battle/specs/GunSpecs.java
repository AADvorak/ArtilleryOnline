package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class GunSpecs implements Specs {

    private double loadTime;

    private double rotationVelocity;

    private double length;

    private double caliber;

    private Map<String, ShellSpecs> availableShells;
}
