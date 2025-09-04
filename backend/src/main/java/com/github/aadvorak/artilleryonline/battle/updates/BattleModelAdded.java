package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.model.*;
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

    private List<BoxModel> boxes;

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

    public void addBox(BoxModel box) {
        if (boxes == null) {
            boxes = new ArrayList<>();
        }
        boxes.add(box);
    }

    public void merge(BattleModelAdded other) {
        if (other.shells != null) {
            if (shells == null) {
                shells = new ArrayList<>();
            }
            shells.addAll(other.shells);
        }
        if (other.explosions != null) {
            if (explosions == null) {
                explosions = new ArrayList<>();
            }
            explosions.addAll(other.explosions);
        }
        if (other.missiles != null) {
            if (missiles == null) {
                missiles = new ArrayList<>();
            }
            missiles.addAll(other.missiles);
        }
        if (other.drones != null) {
            if (drones == null) {
                drones = new ArrayList<>();
            }
            drones.addAll(other.drones);
        }
        if (other.boxes != null) {
            if (boxes == null) {
                boxes = new ArrayList<>();
            }
            boxes.addAll(other.boxes);
        }
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeCollectionOfSerializable(shells);
        stream.writeCollectionOfSerializable(explosions);
        stream.writeCollectionOfSerializable(missiles);
        stream.writeCollectionOfSerializable(drones);
        stream.writeCollectionOfSerializable(boxes);
    }
}
