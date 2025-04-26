package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BattleType implements CompactSerializable {
    TEST_DRIVE((short) 1),
    RANDOM((short) 2),
    ROOM((short) 3),
    DRONE_HUNT((short) 4),
    COLLIDER((short) 5),;

    private final Short id;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
