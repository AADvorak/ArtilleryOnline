package com.github.aadvorak.artilleryonline.battle.processor.damage;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;

public record Hit(Collision collision, double damage, double radius, String nickname) {

    Position position() {
        return collision.getContact().position();
    }

    public static Hit of(Collision collision, ShellCalculations shell) {
        return new Hit(
                collision,
                shell.getModel().getSpecs().getDamage(),
                shell.getModel().getSpecs().getRadius(),
                shell.getModel().getNickname()
        );
    }

    public static Hit of(Collision collision, MissileCalculations missile) {
        return new Hit(
                collision,
                missile.getModel().getSpecs().getDamage(),
                missile.getModel().getSpecs().getRadius(),
                missile.getModel().getNickname()
        );
    }
}
