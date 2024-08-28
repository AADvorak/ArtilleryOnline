package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class GunSpecs implements Specs {

    private double loadTime;

    private double rotationVelocity;

    private Set<ShellSpecs> availableShells;
}
