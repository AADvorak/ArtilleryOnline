package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class ShellVehicleCollisionsDetector {

    public static Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        for (var vehicle : battle.getVehicles()) {
            var collision = detect(shell, vehicle);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }

    private static Collision detect(ShellCalculations shell, VehicleCalculations vehicle) {
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

    private static Collision detectWithVehicle(ShellCalculations shell, VehicleCalculations vehicle) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        return CollisionUtils.detectWithVehicle(shell, position, nextPosition, vehicle);
    }

    private static Collision detectWithWheel(ShellCalculations shell, WheelCalculations wheel) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        return CollisionUtils.detectWithWheel(shell, position, nextPosition, wheel);
    }
}
