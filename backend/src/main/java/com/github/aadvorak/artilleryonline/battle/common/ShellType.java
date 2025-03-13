package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum ShellType implements CompactSerializable {
    AP, HE, SGN, BMB;

    public boolean isAP() {
        return this == AP || this == BMB;
    }

    public boolean isHE() {
        return this == HE || this == BMB;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
