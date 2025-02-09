package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ExplosionState implements State, CompactSerializable {

    private double time;

    private double radius;

    private Position position;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(time);
        stream.writeDouble(radius);
        stream.writeSerializable(position);
        return stream.toByteArray();
    }
}
