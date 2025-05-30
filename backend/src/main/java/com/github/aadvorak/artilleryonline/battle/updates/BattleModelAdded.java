package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelAdded implements CompactSerializable {

    private List<ShellModel> shells;

    private List<ExplosionModel> explosions;

    private List<MissileModel> missiles;

    private List<DroneModel> drones;

    public void addShell(ShellModel shell) {
        if (shells == null) {
            shells = new ArrayList<>();
        }
        shells.add(shell);
    }

    public void addExplosion(ExplosionModel explosion) {
        if (explosions == null) {
            explosions = new ArrayList<>();
        }
        explosions.add(explosion);
    }

    public void addMissile(MissileModel missile) {
        if (missiles == null) {
            missiles = new ArrayList<>();
        }
        missiles.add(missile);
    }

    public void addDrone(DroneModel drone) {
        if (drones == null) {
            drones = new ArrayList<>();
        }
        drones.add(drone);
    }

    public void merge(BattleModelAdded other) {
        shells.addAll(other.shells);
        explosions.addAll(other.explosions);
        missiles.addAll(other.missiles);
        drones.addAll(other.drones);
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeCollectionOfSerializable(shells);
        stream.writeCollectionOfSerializable(explosions);
        stream.writeCollectionOfSerializable(missiles);
        stream.writeCollectionOfSerializable(drones);
    }
}
