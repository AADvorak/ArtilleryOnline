package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.model.*;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BattleModelResponse implements CompactSerializable {

    private Map<Integer, ShellModel> shells;

    private Map<Integer, MissileModel> missiles;

    private Map<Integer, ExplosionModel> explosions;

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    private boolean updated;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeIntegerMapOfSerializable(shells);
        stream.writeIntegerMapOfSerializable(missiles);
        stream.writeIntegerMapOfSerializable(explosions);
        stream.writeSerializable(room);
        stream.writeStringMapOfSerializable(vehicles);
        stream.writeBoolean(updated);
    }
}
