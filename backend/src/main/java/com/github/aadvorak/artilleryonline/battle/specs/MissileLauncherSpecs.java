package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MissileLauncherSpecs implements Specs, CompactSerializable {

    private double prepareToLaunchTime;

    private MissileSpecs missiles;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(prepareToLaunchTime);
        stream.writeSerializableValue(missiles);
    }
}
