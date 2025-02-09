package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public interface Vector extends CompactSerializable {

    double getX();

    double getY();

    default VectorProjections projections(double angle) {
        return new VectorProjections(angle)
                .setNormal(- getX() * Math.sin(angle) + getY() * Math.cos(angle))
                .setTangential(getX() * Math.cos(angle) + getY() * Math.sin(angle));
    }

    default double magnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }

    default double angle() {
        return Math.atan2(getY(), getX());
    }

    @Override
    default byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(getX());
        stream.writeDouble(getY());
        return stream.toByteArray();
    }
}
