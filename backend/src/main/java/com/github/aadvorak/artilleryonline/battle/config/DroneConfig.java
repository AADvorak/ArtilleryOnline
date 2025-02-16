package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.battle.specs.GunSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class DroneConfig implements Config, CompactSerializable {

    private GunSpecs gun;

    private Map<String, Integer> ammo;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(gun);
        stream.writeMap(ammo, stream::writeString, stream::writeInt);
    }
}
