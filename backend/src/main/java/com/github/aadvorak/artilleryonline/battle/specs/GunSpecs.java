package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class GunSpecs implements Specs, CompactSerializable {

    private int ammo;

    private double loadTime;

    private double rotationVelocity;

    private double length;

    private double caliber;

    private Map<String, ShellSpecs> availableShells;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(ammo);
        stream.writeDouble(loadTime);
        stream.writeDouble(rotationVelocity);
        stream.writeDouble(length);
        stream.writeDouble(caliber);
        stream.writeStringMapOfSerializable(availableShells);
    }
}
