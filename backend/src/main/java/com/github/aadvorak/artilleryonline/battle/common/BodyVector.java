package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public interface BodyVector extends CompactSerializable {

    double getX();

    double getY();

    double getAngle();

    @Override
    default byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(getX());
        stream.writeDouble(getY());
        stream.writeDouble(getAngle());
        return stream.toByteArray();
    }
}
