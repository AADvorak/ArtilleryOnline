package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MissileSpecs implements Specs, CompactSerializable {

    private double pushingAcceleration;

    private double correctingAccelerationCoefficient;

    private double minCorrectingVelocity;

    private double anglePrecision;

    private double damage;

    private double radius;

    private double mass;

    private double caliber;

    private double length;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(pushingAcceleration);
        stream.writeDouble(correctingAccelerationCoefficient);
        stream.writeDouble(minCorrectingVelocity);
        stream.writeDouble(anglePrecision);
        stream.writeDouble(damage);
        stream.writeDouble(radius);
        stream.writeDouble(mass);
        stream.writeDouble(caliber);
        stream.writeDouble(length);
        return stream.toByteArray();
    }
}
