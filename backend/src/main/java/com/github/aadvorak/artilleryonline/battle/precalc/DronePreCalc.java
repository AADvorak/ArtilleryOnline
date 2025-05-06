package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.common.Shift;
import com.github.aadvorak.artilleryonline.battle.specs.DroneSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

@Getter
public class DronePreCalc implements BodyPreCalc, CompactSerializable {

    public DronePreCalc(DroneSpecs specs) {
        mass = specs.getMass();
        var density = mass / (Math.pow(specs.getHullRadius(), 2) * Math.PI);
        momentOfInertia = density * Math.PI * Math.pow(specs.getHullRadius(), 4);
        centerOfMassShift = new Shift(0.0, 0.0);
        maxRadius = specs.getEnginesRadius();
    }

    private final double mass;

    private final double momentOfInertia;

    private final Shift centerOfMassShift;

    private final double maxRadius;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(mass);
        stream.writeDouble(momentOfInertia);
        stream.writeSerializableValue(centerOfMassShift);
        stream.writeDouble(maxRadius);
    }
}
