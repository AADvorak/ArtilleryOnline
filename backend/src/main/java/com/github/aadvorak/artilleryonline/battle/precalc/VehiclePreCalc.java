package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.Getter;

@Getter
public class VehiclePreCalc {

    public VehiclePreCalc(VehicleSpecs specs) {
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius(), 2) + Math.pow(specs.getRadius(), 2));
        wheelAngle = Math.atan(specs.getWheelRadius() / specs.getRadius());
        frictionCoefficient = specs.getAcceleration() / specs.getMovingVelocity();
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double frictionCoefficient;
}
