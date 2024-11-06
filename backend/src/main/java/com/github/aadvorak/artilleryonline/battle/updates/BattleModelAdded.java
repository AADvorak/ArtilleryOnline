package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelAdded {

    private List<ShellModel> shells;

    private List<ExplosionModel> explosions;

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
}
