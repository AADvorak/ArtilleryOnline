package com.github.aadvorak.artilleryonline.battle.common.shapes;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class HalfCircleShape implements Shape {

    private static final String NAME = "HalfCircle";

    private double radius;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(getName());
        stream.writeDouble(radius);
    }
}
