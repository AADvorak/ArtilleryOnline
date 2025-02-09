package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.JetType;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class JetSpecs implements CompactSerializable {

    private double capacity;

    private double consumption;

    private double regeneration;

    private double acceleration;

    private JetType type;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeDouble(capacity);
        stream.writeDouble(consumption);
        stream.writeDouble(regeneration);
        stream.writeDouble(acceleration);
        stream.writeSerializable(type);
        return stream.toByteArray();
    }
}
