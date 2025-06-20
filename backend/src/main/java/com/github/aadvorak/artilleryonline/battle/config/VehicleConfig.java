package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.battle.specs.*;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleConfig implements Config, CompactSerializable {

    private GunSpecs gun;

    private JetSpecs jet;

    private DroneSpecs drone;

    private BomberSpecs bomber;

    private Map<String, Integer> ammo;

    private Map<String, Integer> missiles;

    private String color;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(gun);
        stream.writeSerializableValue(jet);
        stream.writeSerializable(drone);
        stream.writeMap(ammo, stream::writeString, stream::writeInt);
        stream.writeMap(missiles, stream::writeString, stream::writeInt);
        stream.writeNullable(color, stream::writeString);
    }
}
