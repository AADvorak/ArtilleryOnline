package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelRemoved implements CompactSerializable {

    private Set<Integer> shellIds;

    private Set<Integer> explosionIds;

    private Set<Integer> missileIds;

    private Set<Integer> droneIds;

    private Set<String> vehicleKeys;

    public void addShellId(Integer shellId) {
        if (shellIds == null) {
            shellIds = new HashSet<>();
        }
        shellIds.add(shellId);
    }

    public void addExplosionId(Integer explosionId) {
        if (explosionIds == null) {
            explosionIds = new HashSet<>();
        }
        explosionIds.add(explosionId);
    }

    public void addMissileId(Integer missileId) {
        if (missileIds == null) {
            missileIds = new HashSet<>();
        }
        missileIds.add(missileId);
    }

    public void addDroneId(Integer droneId) {
        if (droneIds == null) {
            droneIds = new HashSet<>();
        }
        droneIds.add(droneId);
    }

    public void addVehicleKey(String vehicleKey) {
        if (vehicleKeys == null) {
            vehicleKeys = new HashSet<>();
        }
        vehicleKeys.add(vehicleKey);
    }

    public void merge(BattleModelRemoved other) {
        shellIds.addAll(other.shellIds);
        explosionIds.addAll(other.explosionIds);
        missileIds.addAll(other.missileIds);
        droneIds.addAll(other.droneIds);
        vehicleKeys.addAll(other.vehicleKeys);
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
