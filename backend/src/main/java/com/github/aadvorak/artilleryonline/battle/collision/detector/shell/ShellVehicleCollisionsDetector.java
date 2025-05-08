package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShellVehicleCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof ShellCalculations shell) {
            return detectFirst(shell, battle);
        }
        return Set.of();
    }

    private Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
        if (shell.getModel().getState().isStuck()) {
            return Set.of();
        }
        for (var vehicle : battle.getVehicles()) {
            var collision = detect(shell, vehicle);
            if (collision != null) {
                return Set.of(collision);
            }
        }
        return Set.of();
    }

    private Collision detect(ShellCalculations shell, VehicleCalculations vehicle) {
        var collision = detectWithVehicle(shell, vehicle);
        if (collision != null) {
            return collision;
        }
        collision = detectWithWheel(shell, vehicle.getRightWheel());
        if (collision != null) {
            return collision;
        }
        collision = detectWithWheel(shell, vehicle.getLeftWheel());
        return collision;
    }

    private Collision detectWithVehicle(ShellCalculations shell, VehicleCalculations vehicle) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        return CollisionUtils.detectWithVehicle(shell, position, nextPosition, vehicle);
    }

    private Collision detectWithWheel(ShellCalculations shell, WheelCalculations wheel) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        return CollisionUtils.detectWithWheel(shell, position, nextPosition, wheel);
    }
}
