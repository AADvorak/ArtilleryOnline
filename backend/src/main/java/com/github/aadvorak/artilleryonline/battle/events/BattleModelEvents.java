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

    public void addHit(ShellHitEvent hit) {
        if (hits == null) {
            hits = new ArrayList<>();
        }
        hits.add(hit);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return hits == null;
    }
}
