package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum RepairEventType implements CompactSerializable {
    TRACK, TURN_ON_WHEELS, HEAL, REFILL_AMMO;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
