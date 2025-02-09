package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

@Getter
public class VehiclePreCalc implements CompactSerializable {

    public VehiclePreCalc(VehicleSpecs specs) {
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius(), 2) + Math.pow(specs.getHullRadius(), 2));
        wheelAngle = Math.atan(specs.getWheelRadius() / specs.getHullRadius());
        mass = 0.5 * Math.PI * Math.pow(specs.getRadius(), 2) * 4 * specs.getHullRadius() * specs.getWheelRadius();
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double mass;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(wheelDistance);
        stream.writeDouble(wheelAngle);
        stream.writeDouble(mass);
        return stream.toByteArray();
    }
}
