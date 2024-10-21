package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;

@Getter
public class VehiclePreCalc {

    public VehiclePreCalc(VehicleSpecs specs) {
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius(), 2) + Math.pow(specs.getHullRadius(), 2));
        wheelAngle = Math.atan(specs.getWheelRadius() / specs.getHullRadius());
        frictionCoefficient = specs.getAcceleration() / Math.pow(specs.getMovingVelocity(), 2.0);
        mass = 0.5 * Math.PI * Math.pow(specs.getRadius(), 2) * 4 * specs.getHullRadius() * specs.getWheelRadius();
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double frictionCoefficient;

    private final double mass;
}
