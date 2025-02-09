package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellSpecs implements Specs, CompactSerializable {

    private double velocity;

    private double damage;

    private double radius;

    // todo replace with mass
    @Deprecated()
    private double pushCoefficient;

    private double mass;

    private double caliber;

    private ShellType type;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(velocity);
        stream.writeDouble(damage);
        stream.writeDouble(radius);
        stream.writeDouble(pushCoefficient);
        stream.writeDouble(mass);
        stream.writeDouble(caliber);
        stream.writeSerializable(type);
    }
}
