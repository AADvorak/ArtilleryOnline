package com.github.aadvorak.artilleryonline.battle.processor.damage;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public record Hit(Collision collision, Position explosionPosition, double damage, double radius, String nickname) {

    Position position() {
        if (collision != null) {
            return collision.getContact().position();
        }
        return explosionPosition;
    }

    public static Hit of(Collision collision, ShellCalculations shell) {
        return new Hit(
                collision,
                null,
                shell.getModel().getSpecs().getDamage(),
                shell.getModel().getSpecs().getRadius(),
                shell.getModel().getNickname()
        );
    }

    public static Hit of(Collision collision, MissileCalculations missile) {
        return new Hit(
                collision,
                null,
                missile.getModel().getSpecs().getDamage(),
                missile.getModel().getSpecs().getRadius(),
                missile.getModel().getNickname()
        );
    }

    public static Hit explosionOf(VehicleModel vehicleModel) {
        return Hit.of(
                vehicleModel.getState().getPosition().getCenter(),
                vehicleModel.getSpecs().getExplosionDamage(),
                vehicleModel.getPreCalc().getMaxRadius(),
                vehicleModel.getNickname()
        );
    }

    public static Hit explosionOf(BoxModel boxModel, String nickname) {
        return Hit.of(
                boxModel.getState().getPosition().getCenter(),
                boxModel.getSpecs().getExplosionDamage(),
                boxModel.getPreCalc().getMaxRadius(),
                nickname
        );
    }

    public static Hit explosionOf(DroneModel droneModel) {
        return Hit.of(
                droneModel.getState().getPosition().getCenter(),
                droneModel.getSpecs().getExplosionDamage(),
                droneModel.getPreCalc().getMaxRadius(),
                droneModel.getNickname()
        );
    }

    public static Hit of(Position explosionPosition, double damage, double radius, String nickname) {
        return new Hit(
                null,
                explosionPosition,
                damage,
                radius,
                nickname
        );
    }
}
