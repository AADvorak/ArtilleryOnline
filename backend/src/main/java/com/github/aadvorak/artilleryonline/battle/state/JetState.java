package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class JetState implements State, CompactSerializable {

    private double volume;

    private boolean active;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(volume);
        stream.writeBoolean(active);
        return stream.toByteArray();
    }
}
