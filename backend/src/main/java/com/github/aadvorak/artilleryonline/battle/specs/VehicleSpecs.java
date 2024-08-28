package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleSpecs implements Specs {

    private double heatPoints;

    private int ammo;

    private double minAngle;

    private double maxAngle;

    private double movingVelocity;

    private Set<GunSpecs> availableGuns;
}
