package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum CollideObjectType implements CompactSerializable {
    WALL, GROUND, VEHICLE, MISSILE, DRONE, SURFACE;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
