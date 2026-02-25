package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.BoxType;
import com.github.aadvorak.artilleryonline.battle.common.shapes.Shape;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BoxSpecs implements Specs, CompactSerializable {

    private Shape shape;

    private double mass;

    private BoxType type;

    private double explosionDamage;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(shape);
        stream.writeDouble(mass);
        stream.writeSerializableValue(type);
    }
}
