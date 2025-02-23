package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.events.RicochetEvent;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class ShellDroneCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellDroneCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            var drone = (DroneCalculations) collision.getPair().second();
            if (collision.isRicochet()) {
                CollisionUtils.recalculateVelocitiesRigid(collision);
                shell.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
                battle.getModel().getEvents().addRicochet(new RicochetEvent().setShellId(shell.getId()));
            } else {
                shell.getCollisions().add(collision);
                drone.getModel().setDestroyed(true);
                DamageProcessor.processHitDrone(drone, shell, battle);
                pushDrone(collision);
            }
        }
    }

    private static void pushDrone(Collision collision) {
        var shellType = ((ShellCalculations) collision.getPair().first()).getModel().getSpecs().getType();
        if (!ShellType.HE.equals(shellType)) {
            CollisionUtils.pushDroneByDirectHit(collision);
        }
    }
}
