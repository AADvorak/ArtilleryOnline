package com.github.aadvorak.artilleryonline.battle.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelEvents implements CompactSerializable {

    private List<ShellHitEvent> hits;

    private List<VehicleCollideEvent> collides;

    private List<RicochetEvent> ricochets;

    private List<BomberFlyEvent> bomberFlyEvents;

    public void addHit(ShellHitEvent hit) {
        if (hits == null) {
            hits = new ArrayList<>();
        }
        hits.add(hit);
    }

    public void addCollide(VehicleCollideEvent collide) {
        if (collides == null) {
            collides = new ArrayList<>();
        }
        collides.add(collide);
    }

    public void addRicochet(RicochetEvent ricochet) {
        if (ricochets == null) {
            ricochets = new ArrayList<>();
        }
        ricochets.add(ricochet);
    }

    public void addBomberFly(BomberFlyEvent bomberFly) {
        if (bomberFlyEvents == null) {
            bomberFlyEvents = new ArrayList<>();
        }
        bomberFlyEvents.add(bomberFly);
    }

    public void merge(BattleModelEvents other) {
        if (other.hits != null) {
            if (hits == null) {
                hits = new ArrayList<>();
            }
            hits.addAll(other.hits);
        }
        if (other.collides != null) {
            if (collides == null) {
                collides = new ArrayList<>();
            }
            collides.addAll(other.collides);
        }
        if (other.ricochets != null) {
            if (ricochets == null) {
                ricochets = new ArrayList<>();
            }
            ricochets.addAll(other.ricochets);
        }
        if (other.bomberFlyEvents != null) {
            if (bomberFlyEvents == null) {
                bomberFlyEvents = new ArrayList<>();
            }
            bomberFlyEvents.addAll(other.bomberFlyEvents);
        }
    }

    @JsonIgnore
    public boolean isEmpty() {
        return hits == null && collides == null && ricochets == null && bomberFlyEvents == null;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeCollectionOfSerializable(hits);
        stream.writeCollectionOfSerializable(collides);
        stream.writeCollectionOfSerializable(ricochets);
        stream.writeCollectionOfSerializable(bomberFlyEvents);
    }
}
