package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BattleType implements CompactSerializable {
    TEST_DRIVE((short) 1, 5 * 60 * 1000),
    RANDOM((short) 2, 10 * 60 * 1000),
    DEATHMATCH((short) 3, 10 * 60 * 1000),
    DRONE_HUNT((short) 4, 5 * 60 * 1000),
    COLLIDER((short) 5, 10 * 60 * 1000),
    TEAM_ELIMINATION((short) 6, 10 * 60 * 1000),
    ;

    private final Short id;

    private final long duration;

    public boolean isTeam() {
        return this == TEAM_ELIMINATION;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
