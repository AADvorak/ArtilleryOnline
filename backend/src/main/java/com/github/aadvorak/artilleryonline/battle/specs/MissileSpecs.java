package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MissileSpecs implements Specs {

    private double pushingAcceleration;

    private double correctingAccelerationCoefficient;

    private double minCorrectingVelocity;

    private double damage;

    private double radius;

    private double mass;

    private double caliber;

    private double length;
}
