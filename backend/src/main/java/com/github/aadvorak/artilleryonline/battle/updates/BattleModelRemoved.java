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

    private Set<Integer> boxIds;

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

    public void addBoxId(Integer boxId) {
        if (boxIds == null) {
            boxIds = new HashSet<>();
        }
        boxIds.add(boxId);
    }

    public void addVehicleKey(String vehicleKey) {
        if (vehicleKeys == null) {
            vehicleKeys = new HashSet<>();
        }
        vehicleKeys.add(vehicleKey);
    }

    public void merge(BattleModelRemoved other) {
        if (other.shellIds != null) {
            if (shellIds == null) {
                shellIds = new HashSet<>();
            }
            shellIds.addAll(other.shellIds);
        }
        if (other.explosionIds != null) {
            if (explosionIds == null) {
                explosionIds = new HashSet<>();
            }
            explosionIds.addAll(other.explosionIds);
        }
        if (other.missileIds != null) {
            if (missileIds == null) {
                missileIds = new HashSet<>();
            }
            missileIds.addAll(other.missileIds);
        }
        if (other.droneIds != null) {
            if (droneIds == null) {
                droneIds = new HashSet<>();
            }
            droneIds.addAll(other.droneIds);
        }
        if (other.vehicleKeys != null) {
            if (vehicleKeys == null) {
                vehicleKeys = new HashSet<>();
            }
            vehicleKeys.addAll(other.vehicleKeys);
        }
        if (other.boxIds != null) {
            if (boxIds == null) {
                boxIds = new HashSet<>();
            }
            boxIds.addAll(other.boxIds);
        }
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeCollection(shellIds, stream::writeInt);
        stream.writeCollection(explosionIds, stream::writeInt);
        stream.writeCollection(missileIds, stream::writeInt);
        stream.writeCollection(droneIds, stream::writeInt);
        stream.writeCollection(boxIds, stream::writeInt);
        stream.writeCollection(vehicleKeys, stream::writeString);
    }
}
