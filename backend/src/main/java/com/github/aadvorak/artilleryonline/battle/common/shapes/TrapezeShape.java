package com.github.aadvorak.artilleryonline.battle.common.shapes;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TrapezeShape implements Shape {

    private static final String NAME = "Trapeze";

    private double bottomRadius;

    private double topRadius;

    private double height;

    private Double maxSize = null;

    public double getMaxSize() {
        if (maxSize == null) {
            maxSize = Math.max(topRadius, bottomRadius);
            maxSize = Math.max(height, maxSize);
        }
        return maxSize;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(getName());
        stream.writeDouble(bottomRadius);
        stream.writeDouble(topRadius);
        stream.writeDouble(height);
    }

    public static TrapezeShape square(double side) {
        return new TrapezeShape()
                .setBottomRadius(side / 2)
                .setTopRadius(side / 2)
                .setHeight(side);
    }
}
