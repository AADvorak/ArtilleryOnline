package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomSpecs implements Specs, CompactSerializable {

    private Position leftBottom;

    private Position rightTop;

    private double step;

    private double gravityAcceleration;

    private double groundReactionCoefficient;

    private double groundFrictionCoefficient;

    private double airFrictionCoefficient;

    private double groundMaxDepth;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializable(leftBottom);
        stream.writeSerializable(rightTop);
        stream.writeDouble(step);
        stream.writeDouble(gravityAcceleration);
        stream.writeDouble(groundReactionCoefficient);
        stream.writeDouble(groundFrictionCoefficient);
        stream.writeDouble(airFrictionCoefficient);
        stream.writeDouble(groundMaxDepth);
    }
}
