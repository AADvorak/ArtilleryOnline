package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.common.Shift;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

@Getter
public class VehiclePreCalc implements BodyPreCalc, CompactSerializable {

    public VehiclePreCalc(VehicleSpecs specs) {
        var comDistance = (- 2 * Math.PI * Math.pow(specs.getWheelRadius(), 3) + 2 * Math.pow(specs.getRadius(), 3) / 3)
                / (2 * Math.PI * Math.pow(specs.getWheelRadius(), 2) + Math.PI * Math.pow(specs.getRadius(), 2) / 2);
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius() + comDistance, 2) + Math.pow(specs.getHullRadius(), 2));
        wheelAngle = Math.atan((specs.getWheelRadius() + comDistance) / specs.getHullRadius());
        mass = 0.5 * Math.PI * Math.pow(specs.getRadius(), 2) + 2 * Math.PI * Math.pow(specs.getWheelRadius(), 2);
        maxRadius = wheelDistance + specs.getWheelRadius();
        // todo write accurate formula
        momentOfInertia = 1.5 * Math.PI * Math.pow(maxRadius, 4);
        centerOfMassShift = new Shift(comDistance, Math.PI / 2);
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double mass;

    private final double momentOfInertia;

    private final Shift centerOfMassShift;

    private final double maxRadius;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(wheelDistance);
        stream.writeDouble(wheelAngle);
        stream.writeDouble(mass);
        stream.writeDouble(momentOfInertia);
        stream.writeSerializableValue(centerOfMassShift);
        stream.writeDouble(maxRadius);
    }
}
