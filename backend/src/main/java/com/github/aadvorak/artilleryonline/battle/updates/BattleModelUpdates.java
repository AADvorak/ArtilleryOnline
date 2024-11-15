package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelUpdates {

    private BattleModelAdded added;

    private BattleModelRemoved removed;

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

    @JsonIgnore
    public boolean isEmpty() {
        return added == null && removed == null && roomStateUpdates == null;
    }
}
