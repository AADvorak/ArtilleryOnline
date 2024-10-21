package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;

@Getter
public class VehiclePreCalc {

    public VehiclePreCalc(VehicleSpecs specs) {
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius(), 2) + Math.pow(specs.getHullRadius(), 2));
        wheelAngle = Math.atan(specs.getWheelRadius() / specs.getHullRadius());
        frictionCoefficient = specs.getAcceleration() / Math.pow(specs.getMovingVelocity(), 2.0);
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double frictionCoefficient;
}
