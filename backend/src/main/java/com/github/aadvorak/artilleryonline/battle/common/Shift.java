package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public record Shift(double distance, double angle) implements CompactSerializable {

    public Shift inverted() {
        return new Shift(-distance, angle);
    }

    public Shift plusAngle(double addAngle) {
        return new Shift(distance, angle + addAngle);
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(distance);
        stream.writeDouble(angle);
    }
}
