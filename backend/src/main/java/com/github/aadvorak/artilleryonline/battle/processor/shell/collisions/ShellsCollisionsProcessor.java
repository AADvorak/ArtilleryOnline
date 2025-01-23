package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEvent;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEventObject;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class ShellsCollisionsProcessor {

    public static void process(BattleCalculations battle) {
        battle.getShells().forEach(shell -> ShellVehicleCollisionsProcessor.process(shell, battle));

        battle.getShells().forEach(shell -> {
            if (shell.getCollisions().isEmpty()) {
                ShellGroundCollisionsProcessor.process(shell, battle);
            }
        });

        battle.getShells().forEach(shell -> {
            if (!shell.getCollisions().isEmpty()) {
                battle.getModel().getUpdates().removeShell(shell.getId());
                var collision = shell.getCollisions().iterator().next();
                addHitEvent(ShellHitType.of(collision.getPair().second()), shell.getId(),
                        collision.getVehicleId(), battle.getModel());
            }
        });
    }

    private static void addHitEvent(ShellHitType type, Integer shellId, Integer vehicleId, BattleModel battleModel) {
        battleModel.getEvents().addHit(new ShellHitEvent()
                .setShellId(shellId)
                .setObject(new ShellHitEventObject()
                        .setVehicleId(vehicleId)
                        .setType(type)));
    }
}
