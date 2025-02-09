package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BattleStage implements CompactSerializable {
    WAITING(5 * 1000),
    ACTIVE(5 * 60 * 1000),
    FINISHED(0),
    ERROR(0);

    private final long maxTime;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeString(this.name());
        return stream.toByteArray();
    }
}
