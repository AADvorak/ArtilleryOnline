package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShellVehicleCollisionsDetector extends ShellCollisionDetectorBase {

    protected Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
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
