package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelRemoved implements CompactSerializable {

    private List<Integer> shellIds;

    private List<Integer> explosionIds;

    private List<Integer> missileIds;

    private List<Integer> droneIds;

    private List<String> vehicleKeys;

    public void addShellId(Integer shellId) {
        if (shellIds == null) {
            shellIds = new ArrayList<>();
        }
        shellIds.add(shellId);
    }

    public void addExplosionId(Integer explosionId) {
        if (explosionIds == null) {
            explosionIds = new ArrayList<>();
        }
        explosionIds.add(explosionId);
    }

    public void addMissileId(Integer missileId) {
        if (missileIds == null) {
            missileIds = new ArrayList<>();
        }
        missileIds.add(missileId);
    }

    public void addDroneId(Integer droneId) {
        if (droneIds == null) {
            droneIds = new ArrayList<>();
        }
        droneIds.add(droneId);
    }

    public void addVehicleKey(String vehicleKey) {
        if (vehicleKeys == null) {
            vehicleKeys = new ArrayList<>();
        }
        vehicleKeys.add(vehicleKey);
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeCollection(shellIds, stream::writeInt);
        stream.writeCollection(explosionIds, stream::writeInt);
        stream.writeCollection(missileIds, stream::writeInt);
        stream.writeCollection(droneIds, stream::writeInt);
        stream.writeCollection(vehicleKeys, stream::writeString);
    }
}
