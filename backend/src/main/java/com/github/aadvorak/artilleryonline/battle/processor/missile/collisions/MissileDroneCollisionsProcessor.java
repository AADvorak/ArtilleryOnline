package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;

public class MissileDroneCollisionsProcessor {

    public static void process(MissileCalculations missile, BattleCalculations battle) {
        var collision = MissileDroneCollisionsDetector.detectFirst(missile, battle);
        if (collision != null) {
            var drone = (DroneCalculations) collision.getPair().second();
            drone.getModel().setDestroyed(true);
            battle.getModel().getUpdates().removeDrone(drone.getId());
            missile.getCollisions().add(collision);
            DamageProcessor.processHit(missile, battle);
        }
    }
}
