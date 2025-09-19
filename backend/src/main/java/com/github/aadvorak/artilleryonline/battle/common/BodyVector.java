package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public interface BodyVector extends CompactSerializable {

    double getX();

    double getY();

    double getAngle();

    default double magnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getAngle(), 2));
    }

    @Override
    default void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(getX());
        stream.writeDouble(getY());
        stream.writeDouble(getAngle());
    }
}
