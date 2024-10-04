package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.common.IdGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class BattleModel {

    private final IdGenerator idGenerator = new IdGenerator();

    private double currentTimeStepSecs;

    private Map<Integer, ShellModel> shells = new HashMap<>();

    private Map<Integer, ExplosionModel> explosions = new HashMap<>();

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    private boolean updated = false;

    private long lastUpdateTime;

    public void removeShellById(int id) {
        shells.remove(id);
    }

    public void removeExplosionById(int id) {
        explosions.remove(id);
    }

    public void removeVehicleById(int id) {
        vehicles.entrySet().stream()
                .filter(entry -> entry.getValue().getId() == id).findAny()
                .map(Map.Entry::getKey)
                .ifPresent(key -> vehicles.remove(key));
    }

    public void setUpdated(boolean updated) {
        if (updated && !this.updated) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.updated = true;
        }
        if (!updated && this.updated) {
            this.updated = false;
        }
    }
}
