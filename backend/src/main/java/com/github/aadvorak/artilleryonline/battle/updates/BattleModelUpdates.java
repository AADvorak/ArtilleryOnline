package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelUpdates implements CompactSerializable {

    private BattleModelAdded added;

    private BattleModelRemoved removed;

    @Setter
    private List<RoomStateUpdate> roomStateUpdates;

    public void addRoomStateUpdate(RoomStateUpdate roomStateUpdate) {
        if (roomStateUpdates == null) {
            roomStateUpdates = new ArrayList<>();
        }
        roomStateUpdates.add(roomStateUpdate);
    }

    public void addShell(ShellModel shell) {
        if (added == null) {
            added = new BattleModelAdded();
        }
        added.addShell(shell);
    }

    public void addMissile(MissileModel missile) {
        if (added == null) {
            added = new BattleModelAdded();
        }
        added.addMissile(missile);
    }

    public void addDrone(DroneModel drone) {
        if (added == null) {
            added = new BattleModelAdded();
        }
        added.addDrone(drone);
    }

    public void addExplosion(ExplosionModel explosion) {
        if (added == null) {
            added = new BattleModelAdded();
        }
        added.addExplosion(explosion);
    }

    public void removeShell(Integer shellId) {
        if (removed == null) {
            removed = new BattleModelRemoved();
        }
        removed.addShellId(shellId);
    }

    public void removeMissile(Integer missileId) {
        if (removed == null) {
            removed = new BattleModelRemoved();
        }
        removed.addMissileId(missileId);
    }

    public void removeDrone(Integer droneId) {
        if (removed == null) {
            removed = new BattleModelRemoved();
        }
        removed.addDroneId(droneId);
    }

    public void removeExplosion(Integer explosionId) {
        if (removed == null) {
            removed = new BattleModelRemoved();
        }
        removed.addExplosionId(explosionId);
    }

    public void removeVehicle(String vehicleKey) {
        if (removed == null) {
            removed = new BattleModelRemoved();
        }
        removed.addVehicleKey(vehicleKey);
    }

    public void merge(BattleModelUpdates other) {
        if (other.added != null) {
            if (added == null) {
                added = new BattleModelAdded();
            }
            added.merge(other.added);
        }
        if (other.removed != null) {
            if (removed == null) {
                removed = new BattleModelRemoved();
            }
            removed.merge(other.removed);
        }
        if (other.roomStateUpdates != null) {
            if (roomStateUpdates == null) {
                roomStateUpdates = new ArrayList<>();
            }
            roomStateUpdates.addAll(other.roomStateUpdates);
        }
    }

    @JsonIgnore
    public boolean isEmpty() {
        return added == null && removed == null && roomStateUpdates == null;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializable(added);
        stream.writeSerializable(removed);
        stream.writeCollectionOfSerializable(roomStateUpdates);
    }
}
