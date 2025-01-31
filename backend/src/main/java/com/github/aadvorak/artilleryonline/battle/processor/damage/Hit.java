package com.github.aadvorak.artilleryonline.battle.processor.damage;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;

public record Hit(Position position, double damage, double radius, Long userId) {

    public static Hit of(ShellCalculations shell) {
        return new Hit(
                shell.getNext().getPosition(),
                shell.getModel().getSpecs().getDamage(),
                shell.getModel().getSpecs().getRadius(),
                shell.getModel().getUserId()
        );
    }

    public static Hit of(MissileCalculations missile) {
        return new Hit(
                missile.getPosition(),
                missile.getModel().getSpecs().getDamage(),
                missile.getModel().getSpecs().getRadius(),
                missile.getModel().getUserId()
        );
    }

    public static Hit ofHead(MissileCalculations missile) {
        return new Hit(
                missile.getPositions().getHead(),
                missile.getModel().getSpecs().getDamage(),
                missile.getModel().getSpecs().getRadius(),
                missile.getModel().getUserId()
        );
    }
}
