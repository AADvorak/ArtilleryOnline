package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.common.Shift;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

@Getter
public class VehiclePreCalc implements BodyPreCalc, CompactSerializable {

    public VehiclePreCalc(VehicleSpecs specs) {
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius(), 2) + Math.pow(specs.getHullRadius(), 2));
        wheelAngle = Math.atan(specs.getWheelRadius() / specs.getHullRadius());
        // todo fix mistake + instead of *
        mass = 0.5 * Math.PI * Math.pow(specs.getRadius(), 2) * 4 * specs.getHullRadius() * specs.getWheelRadius();
        // todo write accurate formula
        momentOfInertia = Math.PI * Math.pow(specs.getRadius(), 4) / 2;
        centerOfMassShift = new Shift((- 2 * Math.PI * Math.pow(specs.getWheelRadius(), 3) + 2 * Math.pow(specs.getRadius(), 3) / 3)
                / (2 * Math.PI * Math.pow(specs.getWheelRadius(), 2) + Math.PI * Math.pow(specs.getRadius(), 2) / 2), Math.PI / 2);
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double mass;

    private final double momentOfInertia;

    private final Shift centerOfMassShift;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(wheelDistance);
        stream.writeDouble(wheelAngle);
        stream.writeDouble(mass);
    }
}
