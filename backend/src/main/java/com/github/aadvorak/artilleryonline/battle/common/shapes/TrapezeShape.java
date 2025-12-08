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
            var topCornerDistance = Math.sqrt(topRadius * topRadius + height * height);
            maxSize = Math.max(topCornerDistance, bottomRadius);
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

    public static TrapezeShape rectangle(double width, double height) {
        return new TrapezeShape()
                .setBottomRadius(width / 2)
                .setTopRadius(width / 2)
                .setHeight(height);
    }
}
