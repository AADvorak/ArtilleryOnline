package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEvent;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEventObject;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class ShellsCollisionsProcessor {

    public static void process(BattleCalculations battle) {
        battle.getShells().forEach(shell -> {
            if (needProcess(shell)) {
                ShellVehicleCollisionsProcessor.process(shell, battle);
            }
        });

        battle.getShells().forEach(shell -> {
            if (needProcess(shell)) {
                ShellMissileCollisionsProcessor.process(shell, battle);
            }
        });

        battle.getShells().forEach(shell -> {
            if (needProcess(shell)) {
                ShellDroneCollisionsProcessor.process(shell, battle);
            }
        });

        battle.getShells().forEach(shell -> {
            if (needProcess(shell)) {
                ShellGroundCollisionsProcessor.process(shell, battle);
            }
        });

        battle.getShells().forEach(shell -> {
            if (needProcess(shell)) {
                ShellSurfaceCollisionsProcessor.process(shell, battle);
            }
        });

        battle.getShells().forEach(shell -> {
            if (!shell.getCollisions().isEmpty()) {
                battle.getModel().getUpdates().removeShell(shell.getId());
                var collision = shell.getCollisions().iterator().next();
                var hitType = ShellHitType.of(collision.getPair().second());
                if (hitType != null) {
                    addHitEvent(hitType, shell.getId(), collision.getSecondId(), battle.getModel());
                }
            }
        });
    }

    private static void addHitEvent(ShellHitType type, Integer shellId, Integer id, BattleModel battleModel) {
        battleModel.getEvents().addHit(new ShellHitEvent()
                .setShellId(shellId)
                .setObject(new ShellHitEventObject()
                        .setId(id)
                        .setType(type)));
    }

    private static boolean needProcess(ShellCalculations shell) {
        return !shell.getModel().getState().isStuck() && shell.getCollisions().isEmpty();
    }
}
