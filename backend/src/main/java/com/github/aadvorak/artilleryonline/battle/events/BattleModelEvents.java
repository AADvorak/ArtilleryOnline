package com.github.aadvorak.artilleryonline.battle.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelEvents {

    private List<ShellHitEvent> hits;

    private List<VehicleCollideEvent> collides;

    private List<RicochetEvent> ricochets;

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

    @JsonIgnore
    public boolean isEmpty() {
        return hits == null && collides == null && ricochets == null;
    }
}
