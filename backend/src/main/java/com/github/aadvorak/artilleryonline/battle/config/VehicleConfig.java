package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.battle.specs.*;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleConfig implements Config, CompactSerializable {

    private GunSpecs gun;

    private JetSpecs jet;

    private DroneSpecs drone;

    private BomberSpecs bomber;

    private List<AmmoConfig> ammo;

    private MissileLauncherSpecs missileLauncher;

    private String color;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(gun);
        stream.writeSerializableValue(jet);
        stream.writeSerializable(drone);
        stream.writeSerializable(bomber);
        stream.writeCollectionOfSerializable(ammo);
        stream.writeSerializable(missileLauncher);
        stream.writeNullable(color, stream::writeString);
    }
}
