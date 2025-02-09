package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellHitEventObject implements CompactSerializable {

    private Integer vehicleId;

    private ShellHitType type;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeInt(vehicleId);
        stream.writeSerializable(type);
        return stream.toByteArray();
    }
}
