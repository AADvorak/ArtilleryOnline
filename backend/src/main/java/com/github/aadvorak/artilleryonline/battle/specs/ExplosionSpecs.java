package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ExplosionSpecs implements Specs, CompactSerializable {

    private double duration;

    private double radius;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(duration);
        stream.writeDouble(radius);
        return stream.toByteArray();
    }
}
