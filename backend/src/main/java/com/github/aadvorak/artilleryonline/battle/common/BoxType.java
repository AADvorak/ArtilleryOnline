package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum BoxType implements CompactSerializable {
    HP, AMMO;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
