package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.events.RicochetEvent;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class ShellVehicleCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellVehicleCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            var hitObject = collision.getPair().second();
            ((VehicleModel) hitObject.getModel()).setUpdated(true);
            if (collision.isRicochet()) {
                CollisionUtils.recalculateVelocitiesRigid(collision);
                shell.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
                battle.getModel().getEvents().addRicochet(new RicochetEvent().setShellId(shell.getId()));
            } else if (ShellType.SGN.equals(shell.getModel().getSpecs().getType())) {
                CollisionUtils.recalculateVelocitiesRigid(collision, 0.2);
                shell.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
            } else {
                shell.getCollisions().add(collision);
                if (hitObject instanceof VehicleCalculations vehicle) {
                    DamageProcessor.processHitVehicle(vehicle, shell, battle);
                }
                if (hitObject instanceof WheelCalculations wheel) {
                    DamageProcessor.processHitTrack(wheel.getVehicle(), shell, battle);
                }
                pushVehicle(collision);
            }
        }
    }

    private static void pushVehicle(Collision collision) {
        var shellType = ((ShellCalculations) collision.getPair().first()).getModel().getSpecs().getType();
        var shellMass = (ShellType.HE.equals(shellType) ? 0.5 : 1.0) * collision.getPair().first().getMass();
        CollisionUtils.pushVehicleByDirectHit(collision, shellMass);
    }
}
