package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.common.IdGenerator;
import com.github.aadvorak.artilleryonline.battle.events.BattleModelEvents;
import com.github.aadvorak.artilleryonline.battle.statistics.UserBattleStatistics;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
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

    private Map<Integer, MissileModel> missiles = new HashMap<>();

    private Map<Integer, ExplosionModel> explosions = new HashMap<>();

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    private boolean updated = false;

    private long lastUpdateTime;

    private BattleModelUpdates updates;

    private BattleModelEvents events;

    private Map<Long, UserBattleStatistics> statistics;

    public void removeShellById(int id) {
        shells.remove(id);
    }

    public void removeMissileById(int id) {
        missiles.remove(id);
    }

    public void removeExplosionById(int id) {
        explosions.remove(id);
    }

    public void removeVehicleByKey(String key) {
        vehicles.remove(key);
    }

    public String getVehicleKeyById(int id) {
        return vehicles.entrySet().stream()
                .filter(entry -> entry.getValue().getId() == id).findAny()
                .map(Map.Entry::getKey)
                .orElse(null);
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
