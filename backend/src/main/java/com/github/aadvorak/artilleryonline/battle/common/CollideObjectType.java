package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum CollideObjectType implements CompactSerializable {
    WALL, GROUND, VEHICLE, MISSILE;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeString(this.name());
        return stream.toByteArray();
    }
}
