package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEvent;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEventObject;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import org.springframework.stereotype.Component;

@Component
public class ShellCollisionPostprocessor implements CollisionPostprocessor {

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof ShellCalculations shell) {
            for (var collision : shell.getCollisions()) {
                if (collision.isHit()) {
                    battle.getModel().getUpdates().removeShell(shell.getId());
                    var hitType = ShellHitType.of(collision.getPair().second());
                    if (hitType != null) {
                        addHitEvent(hitType, shell.getId(), collision.getSecondId(), battle.getModel());
                    }
                    return;
                }
            }
        }
    }

    private void addHitEvent(ShellHitType type, Integer shellId, Integer id, BattleModel battleModel) {
        battleModel.getEvents().addHit(new ShellHitEvent()
                .setShellId(shellId)
                .setObject(new ShellHitEventObject()
                        .setId(id)
                        .setType(type)));
    }
}
